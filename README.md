  🚀 구현된 주요 기능

  - 실시간 메시징: WebSocket + STOMP 프로토콜로 실시간 채팅
  - 다중 채팅방: 방 ID별로 독립된 채팅방 운영
  - 사용자 입장/퇴장: 채팅방 입장/퇴장 시 시스템 알림
  - 메시지 영속성: MongoDB를 통한 채팅 기록 저장
  - 활성 사용자 관리: Redis로 방별 사용자 목록 및 활동 추적
  - 웹 UI: Tailwind CSS 기반 반응형 채팅 인터페이스

  🛠️ 기술 스택

  - Backend: Java 21, Spring Boot 3.4.0
  - 실시간 통신: Spring WebSocket, STOMP
  - 데이터베이스: MongoDB (채팅기록), PostgreSQL (사용자정보)
  - 캐시: Redis (세션 관리)
  - 프론트엔드: HTML5, TailwindCSS, SockJS, STOMP.js
  - 빌드: Gradle

  📡 연결 방식

  // WebSocket 연결
  const socket = new SockJS(`/ws-chat?username=${username}&roomId=${roomId}`);

  // 메시지 구독
  stompClient.subscribe(`/sub/room/${roomId}`, callback);

  // 메시지 전송
  stompClient.publish({
      destination: '/pub/room/chat/send',
      body: JSON.stringify(chatMessage)
  });

  🏗️ 인증 시스템

  - 간단 인증: 쿼리 파라미터로 username, roomId 전달
  - 세션 검증: WebSocket 핸드셰이크 시 사용자 정보 검증
  - 메시지 검증: 전송자 일치 여부 확인

  Note: JWT 인증은 코드에 있지만 현재 비활성화 상태입니다.

  ⚡ 빠른 시작

  # 애플리케이션 실행
  ./gradlew bootRun

  # 브라우저에서 접속
  http://localhost:8082
