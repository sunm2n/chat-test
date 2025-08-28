package com.example.chatapp.domain.chatroom.service;

import com.example.chatapp.domain.chatroom.dto.ChatRoomResponse;
import com.example.chatapp.domain.chatroom.dto.CreateChatRoomRequest;
import com.example.chatapp.domain.chatroom.entity.ChatRoom;
import com.example.chatapp.domain.chatroom.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatRoomService {
    
    private final ChatRoomRepository chatRoomRepository;
    
    public List<ChatRoomResponse> getAllChatRooms() {
        return chatRoomRepository.findAllOrderByUpdatedAtDesc().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    public ChatRoomResponse getChatRoomByRoomId(String roomId) {
        ChatRoom chatRoom = chatRoomRepository.findByRoomId(roomId)
                .orElseThrow(() -> new RuntimeException("채팅방을 찾을 수 없습니다."));
        return convertToResponse(chatRoom);
    }
    
    public ChatRoomResponse createChatRoom(CreateChatRoomRequest request, String createdBy) {
        String roomId = generateUniqueRoomId();
        
        ChatRoom chatRoom = ChatRoom.builder()
                .roomId(roomId)
                .roomName(request.getRoomName())
                .description(request.getDescription())
                .createdBy(createdBy)
                .participantCount(0)
                .build();
        
        ChatRoom savedRoom = chatRoomRepository.save(chatRoom);
        return convertToResponse(savedRoom);
    }
    
    public void deleteChatRoom(String roomId) {
        if (!chatRoomRepository.existsByRoomId(roomId)) {
            throw new RuntimeException("채팅방을 찾을 수 없습니다.");
        }
        chatRoomRepository.deleteByRoomId(roomId);
    }
    
    public void incrementParticipantCount(String roomId) {
        ChatRoom chatRoom = chatRoomRepository.findByRoomId(roomId)
                .orElseThrow(() -> new RuntimeException("채팅방을 찾을 수 없습니다."));
        chatRoom.setParticipantCount(chatRoom.getParticipantCount() + 1);
        chatRoomRepository.save(chatRoom);
    }
    
    public void decrementParticipantCount(String roomId) {
        ChatRoom chatRoom = chatRoomRepository.findByRoomId(roomId)
                .orElseThrow(() -> new RuntimeException("채팅방을 찾을 수 없습니다."));
        int currentCount = chatRoom.getParticipantCount();
        if (currentCount > 0) {
            chatRoom.setParticipantCount(currentCount - 1);
            chatRoomRepository.save(chatRoom);
        }
    }
    
    private String generateUniqueRoomId() {
        String roomId;
        do {
            roomId = UUID.randomUUID().toString().substring(0, 8);
        } while (chatRoomRepository.existsByRoomId(roomId));
        return roomId;
    }
    
    private ChatRoomResponse convertToResponse(ChatRoom chatRoom) {
        return ChatRoomResponse.builder()
                .id(chatRoom.getId())
                .roomId(chatRoom.getRoomId())
                .roomName(chatRoom.getRoomName())
                .description(chatRoom.getDescription())
                .createdBy(chatRoom.getCreatedBy())
                .createdAt(chatRoom.getCreatedAt())
                .updatedAt(chatRoom.getUpdatedAt())
                .participantCount(chatRoom.getParticipantCount())
                .build();
    }
}