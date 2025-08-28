package com.example.chatapp.domain.chatroom.controller;

import com.example.chatapp.domain.chatroom.dto.ChatRoomResponse;
import com.example.chatapp.domain.chatroom.dto.CreateChatRoomRequest;
import com.example.chatapp.domain.chatroom.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chatrooms")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ChatRoomController {
    
    private final ChatRoomService chatRoomService;
    
    @GetMapping
    public ResponseEntity<List<ChatRoomResponse>> getAllChatRooms() {
        List<ChatRoomResponse> chatRooms = chatRoomService.getAllChatRooms();
        return ResponseEntity.ok(chatRooms);
    }
    
    @GetMapping("/{roomId}")
    public ResponseEntity<ChatRoomResponse> getChatRoom(@PathVariable String roomId) {
        ChatRoomResponse chatRoom = chatRoomService.getChatRoomByRoomId(roomId);
        return ResponseEntity.ok(chatRoom);
    }
    
    @PostMapping
    public ResponseEntity<ChatRoomResponse> createChatRoom(
            @RequestBody CreateChatRoomRequest request,
            @RequestParam(required = false, defaultValue = "anonymous") String createdBy) {
        ChatRoomResponse chatRoom = chatRoomService.createChatRoom(request, createdBy);
        return ResponseEntity.ok(chatRoom);
    }
    
    @DeleteMapping("/{roomId}")
    public ResponseEntity<Void> deleteChatRoom(@PathVariable String roomId) {
        chatRoomService.deleteChatRoom(roomId);
        return ResponseEntity.ok().build();
    }
}