package com.example.chatapp.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class SimpleHandshakeInterceptor implements HandshakeInterceptor {

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        
        String username = extractUsername(request);
        String roomId = extractRoomId(request);
        
        if (username == null || username.trim().isEmpty()) {
            log.warn("No username found in handshake request");
            return false;
        }
        
        if (roomId == null || roomId.trim().isEmpty()) {
            log.warn("No room ID found in handshake request");
            return false;
        }

        // URL 디코딩된 사용자명을 저장
        String decodedUsername = URLDecoder.decode(username, StandardCharsets.UTF_8);
        
        // 사용자 ID를 username과 동일하게 설정 (간단화)
        attributes.put("userId", decodedUsername);
        attributes.put("username", decodedUsername);
        attributes.put("roomId", roomId);

        log.info("WebSocket handshake successful for user: {} in room: {}", decodedUsername, roomId);
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
        if (exception != null) {
            log.error("WebSocket handshake failed: {}", exception.getMessage());
        }
    }

    private String extractUsername(ServerHttpRequest request) {
        Map<String, List<String>> queryParams = UriComponentsBuilder
                .fromUri(request.getURI())
                .build()
                .getQueryParams();

        List<String> usernameParams = queryParams.get("username");
        if (usernameParams != null && !usernameParams.isEmpty()) {
            return usernameParams.get(0);
        }

        return null;
    }

    private String extractRoomId(ServerHttpRequest request) {
        Map<String, List<String>> queryParams = UriComponentsBuilder
                .fromUri(request.getURI())
                .build()
                .getQueryParams();

        List<String> roomIdParams = queryParams.get("roomId");
        if (roomIdParams != null && !roomIdParams.isEmpty()) {
            return roomIdParams.get(0);
        }

        return null;
    }
}