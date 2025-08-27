# Real-time Chat Application

## 개요
사용자들 간의 실시간 채팅 기능을 제공하는 WebSocket 기반 채팅 애플리케이션입니다.

## 주요 기능
- 실시간 메시지 송수신
- 채팅방 입장/퇴장
- JWT 기반 사용자 인증
- 다중 채팅방 지원
- WebSocket + STOMP 프로토콜 활용

## 기술 스택

### Backend
- **Java 21**
- **Spring Boot 3.4.0**
- **Spring WebSocket** - WebSocket 연결 및 STOMP 메시징
- **Spring Security** - JWT 인증
- **PostgreSQL** - 사용자 및 채팅 데이터 저장
- **Redis** - 세션 관리 및 실시간 메시지 브로커
- **MongoDB** - 채팅 메시지 로그 저장

### 주요 의존성
```gradle
implementation 'org.springframework.boot:spring-boot-starter-websocket'
implementation 'org.springframework.boot:spring-boot-starter-security'
implementation 'org.springframework.boot:spring-boot-starter-data-redis'
implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
implementation 'org.springframework.boot:spring-boot-starter-data-mongodb'
```

## 아키텍처 구조

### WebSocket 설정 (WebSocketConfig)
- **연결 엔드포인트**: `/ws-chat`
- **메시지 브로커**: `/sub` (구독)
- **애플리케이션 목적지**: `/pub` (발행)
- **SockJS 지원**: fallback 메커니즘
- **Heartbeat**: 30초 간격

### 채팅 메시지 흐름
```
Client → /pub/room/chat/send → Controller → Service → /sub/room/{roomId} → All Clients
```

### JWT 인증 (JwtHandshakeInterceptor)
- WebSocket 연결 시 JWT 토큰 검증
- 쿼리 파라미터 또는 Authorization 헤더에서 토큰 추출
- 블랙리스트 토큰 차단
- 사용자 ID 및 방 ID 세션 속성 저장

## API 엔드포인트

### WebSocket 연결
```
GET /ws-chat?token={JWT_TOKEN}&roomId={ROOM_ID}
```

### STOMP 메시지 경로

#### 채팅 메시지 전송
```
SEND /pub/room/chat/send
{
  "roomId": "room-001",
  "message": "안녕하세요!",
  "senderId": "user123"
}
```

#### 채팅방 입장
```
SEND /pub/room/chat/join
{
  "roomId": "room-001",
  "senderId": "user123"
}
```

#### 채팅방 퇴장
```
SEND /pub/room/chat/leave
{
  "roomId": "room-001",
  "senderId": "user123"
}
```

### 구독 경로
```
SUBSCRIBE /sub/room/{roomId}
```

## 데이터 모델

### 메시지 구조 (UnifiedMessageRequest)
```json
{
  "roomId": "room-001",
  "message": "메시지 내용",
  "senderId": "user123",
  "messageType": "CHAT",
  "timestamp": "2024-01-01T00:00:00"
}
```

### 응답 메시지 구조
```json
{
  "roomId": "room-001",
  "message": "메시지 내용",
  "senderId": "user123",
  "senderName": "홍길동",
  "messageType": "CHAT",
  "timestamp": "2024-01-01T00:00:00"
}
```

## 설정 및 실행

### 1. 환경 변수 설정
```properties
# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/chatdb
spring.datasource.username=postgres
spring.datasource.password=password

# Redis
spring.data.redis.host=localhost
spring.data.redis.port=6379

# MongoDB
spring.data.mongodb.uri=mongodb://localhost:27017/chatdb

# JWT
jwt.secret.key=your-secret-key
jwt.expiration=86400
```

### 2. 애플리케이션 실행
```bash
./gradlew bootRun
```

### 3. WebSocket 연결 테스트
```javascript
// JavaScript 클라이언트 예제
const socket = new SockJS('/ws-chat?token=YOUR_JWT_TOKEN&roomId=room-001');
const stompClient = Stomp.over(socket);

stompClient.connect({}, function(frame) {
    // 채팅방 구독
    stompClient.subscribe('/sub/room/room-001', function(message) {
        const chatMessage = JSON.parse(message.body);
        console.log('받은 메시지:', chatMessage);
    });
    
    // 메시지 전송
    stompClient.send('/pub/room/chat/send', {}, JSON.stringify({
        roomId: 'room-001',
        message: '안녕하세요!',
        senderId: 'user123'
    }));
});
```

## 보안 고려사항

### JWT 인증
- WebSocket 연결 시 JWT 토큰 필수
- 토큰 만료 검증
- 블랙리스트 토큰 차단

### 메시지 검증
- 입력 데이터 유효성 검사 (`@Valid`)
- XSS 방지를 위한 메시지 필터링
- 스팸 방지 메커니즘

### 연결 관리
- 연결 시간 제한 (20초)
- 메시지 크기 제한 (128KB)
- 버퍼 크기 제한 (512KB)

## 확장 가능성

### 채팅방 유형 확장
현재 구조는 다양한 채팅방 유형을 지원합니다:
- 일반 채팅방 (`/pub/room/chat/*`)
- 그룹 채팅방
- 비공개 채팅방
- 게임 채팅방

### 메시지 유형 확장
- 텍스트 메시지
- 이미지 메시지
- 파일 전송
- 시스템 메시지

### 알림 기능
- 실시간 알림
- 푸시 알림
- 이메일 알림

## 모니터링 및 로깅

### 로그 구성
```properties
logging.level.org.com.dungeontalk.websocket=DEBUG
logging.level.org.springframework.web.socket=DEBUG
```

### 메트릭스
- 동시 연결 수
- 메시지 처리량
- 오류율
- 응답 시간

## 트러블슈팅

### 일반적인 문제
1. **WebSocket 연결 실패**
    - JWT 토큰 검증
    - CORS 설정 확인
    - 방화벽 설정 확인

2. **메시지 전송 실패**
    - 메시지 형식 검증
    - 권한 확인
    - 네트워크 연결 상태

3. **성능 문제**
    - 연결 수 모니터링
    - 메시지 큐 상태 확인
    - 리소스 사용량 점검