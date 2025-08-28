package com.example.chatapp.domain.chat.service;

import com.example.chatapp.domain.chat.dto.ChatMessageResponse;
import com.example.chatapp.domain.chat.dto.MessageType;
import com.example.chatapp.domain.chat.dto.UnifiedMessageRequest;
import com.example.chatapp.domain.chat.entity.ChatMessage;
import com.example.chatapp.domain.chat.repository.ChatMessageRepository;
import com.example.chatapp.domain.chatroom.service.ChatRoomService;
import com.example.chatapp.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final ChatRoomService chatRoomService;

    public void processMessage(UnifiedMessageRequest request) {
        try {
            ChatMessageResponse response = buildMessageResponse(request);
            
            if (response == null) {
                log.warn("Failed to build message response for request: {}", request);
                return;
            }

            saveMessageToDatabase(response);
            
            sendMessageToRoom(response);
            
            updateRoomActivity(request.getRoomId(), request.getSenderId());
            
        } catch (Exception e) {
            log.error("Error processing message: {}", e.getMessage(), e);
        }
    }

    private ChatMessageResponse buildMessageResponse(UnifiedMessageRequest request) {
        String senderName = getUserDisplayName(request.getSenderId());
        if (senderName == null) {
            log.warn("User not found for ID: {}", request.getSenderId());
            return null;
        }

        String sanitizedMessage = sanitizeMessage(request.getMessage());
        
        return ChatMessageResponse.builder()
                .roomId(request.getRoomId())
                .message(sanitizedMessage)
                .senderId(request.getSenderId())
                .senderName(senderName)
                .messageType(request.getMessageType())
                .timestamp(LocalDateTime.now())
                .build();
    }

    private void saveMessageToDatabase(ChatMessageResponse response) {
        try {
            ChatMessage chatMessage = ChatMessage.builder()
                    .roomId(response.getRoomId())
                    .message(response.getMessage())
                    .senderId(response.getSenderId())
                    .senderName(response.getSenderName())
                    .messageType(response.getMessageType())
                    .timestamp(response.getTimestamp())
                    .build();

            chatMessageRepository.save(chatMessage);
            log.debug("Message saved to database: {}", chatMessage.getId());
        } catch (Exception e) {
            log.error("Error saving message to database: {}", e.getMessage());
        }
    }

    private void sendMessageToRoom(ChatMessageResponse response) {
        try {
            String destination = "/sub/room/" + response.getRoomId();
            messagingTemplate.convertAndSend(destination, response);
            log.debug("Message sent to room {}: {}", response.getRoomId(), response.getMessage());
        } catch (Exception e) {
            log.error("Error sending message to room: {}", e.getMessage());
        }
    }

    private String getUserDisplayName(String userId) {
        // 간단화: userId를 그대로 displayName으로 사용
        return userId != null ? userId : "Unknown User";
    }

    private String sanitizeMessage(String message) {
        if (message == null) return "";
        
        String sanitized = HtmlUtils.htmlEscape(message);
        
        if (sanitized.length() > 1000) {
            sanitized = sanitized.substring(0, 1000) + "...";
        }
        
        return sanitized.trim();
    }

    private void updateRoomActivity(String roomId, String userId) {
        try {
            String key = "room:activity:" + roomId;
            redisTemplate.opsForZSet().add(key, userId, System.currentTimeMillis());
            redisTemplate.expire(key, 24, TimeUnit.HOURS);
        } catch (Exception e) {
            log.error("Error updating room activity: {}", e.getMessage());
        }
    }

    public void handleUserJoin(String roomId, String userId) {
        try {
            String senderName = getUserDisplayName(userId);
            if (senderName == null) return;

            ChatMessageResponse joinMessage = ChatMessageResponse.builder()
                    .roomId(roomId)
                    .message(senderName + "님이 채팅방에 입장했습니다.")
                    .senderId("SYSTEM")
                    .senderName("System")
                    .messageType(MessageType.JOIN)
                    .timestamp(LocalDateTime.now())
                    .build();

            saveMessageToDatabase(joinMessage);
            sendMessageToRoom(joinMessage);
            
            addUserToRoom(roomId, userId);
            updateRoomActivity(roomId, userId);
            
            // 채팅방 참여자 수 증가
            try {
                chatRoomService.incrementParticipantCount(roomId);
            } catch (Exception e) {
                log.warn("Failed to increment participant count for room {}: {}", roomId, e.getMessage());
            }
            
            log.info("User {} joined room {}", userId, roomId);
        } catch (Exception e) {
            log.error("Error handling user join: {}", e.getMessage());
        }
    }

    public void handleUserLeave(String roomId, String userId) {
        try {
            String senderName = getUserDisplayName(userId);
            if (senderName == null) return;

            ChatMessageResponse leaveMessage = ChatMessageResponse.builder()
                    .roomId(roomId)
                    .message(senderName + "님이 채팅방을 떠났습니다.")
                    .senderId("SYSTEM")
                    .senderName("System")
                    .messageType(MessageType.LEAVE)
                    .timestamp(LocalDateTime.now())
                    .build();

            saveMessageToDatabase(leaveMessage);
            sendMessageToRoom(leaveMessage);
            
            removeUserFromRoom(roomId, userId);
            
            // 채팅방 참여자 수 감소
            try {
                chatRoomService.decrementParticipantCount(roomId);
            } catch (Exception e) {
                log.warn("Failed to decrement participant count for room {}: {}", roomId, e.getMessage());
            }
            
            log.info("User {} left room {}", userId, roomId);
        } catch (Exception e) {
            log.error("Error handling user leave: {}", e.getMessage());
        }
    }

    private void addUserToRoom(String roomId, String userId) {
        try {
            String key = "room:users:" + roomId;
            redisTemplate.opsForSet().add(key, userId);
            redisTemplate.expire(key, 24, TimeUnit.HOURS);
        } catch (Exception e) {
            log.error("Error adding user to room: {}", e.getMessage());
        }
    }

    private void removeUserFromRoom(String roomId, String userId) {
        try {
            String key = "room:users:" + roomId;
            redisTemplate.opsForSet().remove(key, userId);
        } catch (Exception e) {
            log.error("Error removing user from room: {}", e.getMessage());
        }
    }

    public Set<String> getRoomUsers(String roomId) {
        try {
            String key = "room:users:" + roomId;
            return redisTemplate.opsForSet().members(key);
        } catch (Exception e) {
            log.error("Error getting room users: {}", e.getMessage());
            return Set.of();
        }
    }

    public List<ChatMessage> getChatHistory(String roomId, int limit) {
        try {
            List<ChatMessage> messages = chatMessageRepository.findByRoomIdOrderByTimestampDesc(roomId);
            return messages.stream()
                    .limit(limit)
                    .toList();
        } catch (Exception e) {
            log.error("Error getting chat history: {}", e.getMessage());
            return List.of();
        }
    }
}