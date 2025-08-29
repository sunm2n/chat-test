package com.example.chatapp.global.config.websocket;

import com.example.chatapp.global.security.interceptor.SimpleHandshakeInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final SimpleHandshakeInterceptor simpleHandshakeInterceptor;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/sub");
        config.setApplicationDestinationPrefixes("/pub");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 채팅용 WebSocket (JWT 인증 필요)
        registry.addEndpoint("/ws-chat")
                .addInterceptors(simpleHandshakeInterceptor)
                .setAllowedOriginPatterns("*")
                .withSockJS()
                .setHeartbeatTime(30000)
                .setDisconnectDelay(5000)
                .setClientLibraryUrl("https://cdn.jsdelivr.net/npm/sockjs-client@1/dist/sockjs.min.js");
        
        // 채팅방 목록 업데이트용 WebSocket (인증 없음)
        registry.addEndpoint("/ws-roomlist")
                .setAllowedOriginPatterns("*")
                .withSockJS()
                .setHeartbeatTime(30000)
                .setDisconnectDelay(5000)
                .setClientLibraryUrl("https://cdn.jsdelivr.net/npm/sockjs-client@1/dist/sockjs.min.js");
    }

    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registration) {
        registration.setMessageSizeLimit(128 * 1024)
                .setSendBufferSizeLimit(512 * 1024)
                .setSendTimeLimit(20 * 1000);
    }
}