package com.example.chatapp.controller;

import com.example.chatapp.dto.MessageType;
import com.example.chatapp.dto.UnifiedMessageRequest;
import com.example.chatapp.service.ChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @MessageMapping("/room/chat/send")
    public void sendMessage(@Valid @Payload UnifiedMessageRequest message, 
                           SimpMessageHeaderAccessor headerAccessor) {
        try {
            String userId = (String) headerAccessor.getSessionAttributes().get("userId");
            String roomId = (String) headerAccessor.getSessionAttributes().get("roomId");
            
            if (userId == null || roomId == null) {
                log.warn("Missing user ID or room ID in session attributes");
                return;
            }
            
            if (!roomId.equals(message.getRoomId())) {
                log.warn("Room ID mismatch: session={}, message={}", roomId, message.getRoomId());
                return;
            }
            
            if (!userId.equals(message.getSenderId())) {
                log.warn("User ID mismatch: session={}, message={}", userId, message.getSenderId());
                return;
            }
            
            message.setMessageType(MessageType.CHAT);
            chatService.processMessage(message);
            
        } catch (Exception e) {
            log.error("Error processing chat message: {}", e.getMessage(), e);
        }
    }

    @MessageMapping("/room/chat/join")
    public void joinRoom(@Valid @Payload UnifiedMessageRequest message,
                        SimpMessageHeaderAccessor headerAccessor) {
        try {
            String userId = (String) headerAccessor.getSessionAttributes().get("userId");
            String roomId = (String) headerAccessor.getSessionAttributes().get("roomId");
            
            if (userId == null || roomId == null) {
                log.warn("Missing user ID or room ID in session attributes for join");
                return;
            }
            
            if (!roomId.equals(message.getRoomId()) || !userId.equals(message.getSenderId())) {
                log.warn("ID mismatch in join request");
                return;
            }
            
            chatService.handleUserJoin(roomId, userId);
            
        } catch (Exception e) {
            log.error("Error processing join message: {}", e.getMessage(), e);
        }
    }

    @MessageMapping("/room/chat/leave")
    public void leaveRoom(@Valid @Payload UnifiedMessageRequest message,
                         SimpMessageHeaderAccessor headerAccessor) {
        try {
            String userId = (String) headerAccessor.getSessionAttributes().get("userId");
            String roomId = (String) headerAccessor.getSessionAttributes().get("roomId");
            
            if (userId == null || roomId == null) {
                log.warn("Missing user ID or room ID in session attributes for leave");
                return;
            }
            
            if (!roomId.equals(message.getRoomId()) || !userId.equals(message.getSenderId())) {
                log.warn("ID mismatch in leave request");
                return;
            }
            
            chatService.handleUserLeave(roomId, userId);
            
        } catch (Exception e) {
            log.error("Error processing leave message: {}", e.getMessage(), e);
        }
    }
}