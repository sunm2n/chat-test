package com.example.chatapp.global.security.interceptor;

import com.example.chatapp.global.security.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtHandshakeInterceptor implements HandshakeInterceptor {

    private final JwtService jwtService;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        
        String token = extractToken(request);
        if (token == null) {
            log.warn("No JWT token found in handshake request");
            return false;
        }

        if (!jwtService.validateToken(token)) {
            log.warn("Invalid JWT token in handshake request");
            return false;
        }

        String userId = jwtService.extractUserId(token);
        if (userId == null) {
            log.warn("Unable to extract user ID from token");
            return false;
        }

        String roomId = extractRoomId(request);
        if (roomId == null) {
            log.warn("No room ID found in handshake request");
            return false;
        }

        attributes.put("userId", userId);
        attributes.put("roomId", roomId);
        attributes.put("token", token);

        log.info("WebSocket handshake successful for user: {} in room: {}", userId, roomId);
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
        if (exception != null) {
            log.error("WebSocket handshake failed: {}", exception.getMessage());
        }
    }

    private String extractToken(ServerHttpRequest request) {
        String authHeader = request.getHeaders().getFirst("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }

        Map<String, List<String>> queryParams = UriComponentsBuilder
                .fromUri(request.getURI())
                .build()
                .getQueryParams();

        List<String> tokenParams = queryParams.get("token");
        if (tokenParams != null && !tokenParams.isEmpty()) {
            return tokenParams.get(0);
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