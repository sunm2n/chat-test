# 실시간 채팅 앱 데이터베이스 설정 가이드

## 개요
Spring Boot 실시간 채팅 애플리케이션을 위한 PostgreSQL, Redis, MongoDB 데이터베이스 설정 가이드입니다.

## 필요한 서비스
- **PostgreSQL**: 메인 데이터베이스 (채팅방, 사용자 정보)
- **Redis**: 세션 관리 및 캐싱
- **MongoDB**: 채팅 메시지 저장

## 빠른 시작

### 1. Docker Compose로 실행
```bash
docker-compose up -d
```

### 2. 서비스 확인
```bash
docker-compose ps
```

### 3. 애플리케이션 설정
Spring Boot `application.yml`에 다음 설정을 추가:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/chatdb
    username: postgres
    password: password
    driver-class-name: org.postgresql.Driver

  data:
    redis:
      host: localhost
      port: 6379

    mongodb:
      uri: mongodb://localhost:27017/chatdb
```

## 서비스 정보

### PostgreSQL
- **포트**: 5432
- **데이터베이스**: chatdb
- **사용자명**: postgres
- **비밀번호**: password

### Redis
- **포트**: 6379
- **용도**: 세션 관리, 캐싱

### MongoDB
- **포트**: 27017
- **데이터베이스**: chatdb
- **용도**: 채팅 메시지 저장

## 중지 및 정리
```bash
# 서비스 중지
docker-compose down

# 볼륨까지 삭제
docker-compose down -v
```