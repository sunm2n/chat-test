package com.example.chatapp.domain.chat.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UnifiedMessageRequest {
    
    @NotBlank(message = "Room ID is required")
    private String roomId;
    
    private String message;
    
    @NotBlank(message = "Sender ID is required")
    private String senderId;
    
    @NotNull(message = "Message type is required")
    private MessageType messageType;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;
    
    public UnifiedMessageRequest() {
        this.timestamp = LocalDateTime.now();
    }
}