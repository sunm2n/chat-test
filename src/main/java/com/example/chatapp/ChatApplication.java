package com.example.chatapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.example.chatapp.repository.jpa")
@EnableMongoRepositories(basePackages = "com.example.chatapp.repository.mongo")
public class ChatApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(ChatApplication.class, args);
    }
}