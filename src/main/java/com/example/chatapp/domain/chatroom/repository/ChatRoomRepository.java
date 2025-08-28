package com.example.chatapp.domain.chatroom.repository;

import com.example.chatapp.domain.chatroom.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    
    Optional<ChatRoom> findByRoomId(String roomId);
    
    @Query("SELECT c FROM ChatRoom c ORDER BY c.updatedAt DESC")
    List<ChatRoom> findAllOrderByUpdatedAtDesc();
    
    List<ChatRoom> findByCreatedBy(String createdBy);
    
    boolean existsByRoomId(String roomId);
    
    void deleteByRoomId(String roomId);
}