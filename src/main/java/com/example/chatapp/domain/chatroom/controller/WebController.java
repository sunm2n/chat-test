package com.example.chatapp.domain.chatroom.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {
    
    @GetMapping("/")
    public String index() {
        return "redirect:/chatrooms";
    }
    
    @GetMapping("/chatrooms")
    public String chatRoomList() {
        return "chatroom-list";
    }
    
    @GetMapping("/chat/{roomId}")
    public String chatRoom() {
        return "chat";
    }
}