package com.example.chatapp.domain.chat.controller;

import com.example.chatapp.domain.chat.entity.ChatMessage;
import com.example.chatapp.domain.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatRestController {

    private final ChatService chatService;

    @GetMapping("/rooms/{roomId}/history")
    public ResponseEntity<List<ChatMessage>> getChatHistory(
            @PathVariable String roomId,
            @RequestParam(defaultValue = "50") int limit) {
        
        List<ChatMessage> messages = chatService.getChatHistory(roomId, limit);
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/rooms/{roomId}/users")
    public ResponseEntity<Set<String>> getRoomUsers(@PathVariable String roomId) {
        Set<String> users = chatService.getRoomUsers(roomId);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Chat service is running");
    }
}