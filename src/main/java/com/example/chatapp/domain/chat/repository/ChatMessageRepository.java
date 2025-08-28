package com.example.chatapp.domain.chat.repository;

import com.example.chatapp.domain.chat.entity.ChatMessage;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {
    
    List<ChatMessage> findByRoomIdOrderByTimestampDesc(String roomId);
    
    List<ChatMessage> findByRoomIdAndTimestampAfterOrderByTimestampAsc(String roomId, LocalDateTime timestamp);
    
    @Query("{'roomId': ?0, 'timestamp': {$gte: ?1, $lte: ?2}}")
    List<ChatMessage> findByRoomIdAndTimestampBetween(String roomId, LocalDateTime start, LocalDateTime end);
    
    void deleteByRoomIdAndTimestampBefore(String roomId, LocalDateTime timestamp);
}