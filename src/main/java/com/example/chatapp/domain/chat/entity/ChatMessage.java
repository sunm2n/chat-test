package com.example.chatapp.domain.chat.entity;

import com.example.chatapp.domain.chat.dto.MessageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "chat_messages")
public class ChatMessage {
    
    @Id
    private String id;
    private String roomId;
    private String message;
    private String senderId;
    private String senderName;
    private MessageType messageType;
    private LocalDateTime timestamp;
}