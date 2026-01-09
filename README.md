## 실시간 채팅 (Real-time Chat with STOMP)

WebSocket과 STOMP 프로토콜을 활용하여 저지연(Low-Latency) 실시간 통신 환경을 구축하고, 인터셉터를 통해 보안성을 확보했습니다.

### 기술 스택 및 전략
* **Protocol**: STOMP over WebSocket
* **Library**: Spring Messaging, SockJS (브라우저 호환성 확보)
* **Auth**: 연결 시점에 JWT 기반 유저 식별 및 세션 관리

### 주요 코드 설명

#### WebSocket & Broker 설정 (`WebSocketConfig`)
메시지 송수신 경로를 분리하고, 보안을 위한 인터셉터를 등록했습니다.
```java
@Override
public void configureMessageBroker(MessageBrokerRegistry registry) {
  registry.enableSimpleBroker("/sub"); // 구독 경로 (메시지 수신)
  registry.setApplicationDestinationPrefixes("/pub"); // 송신 경로 (메시지 발송)
}
```
#### STOMP 인터셉터 기반 인증 (StompHandler)
웹소켓 연결(CONNECT) 시점에 클라이언트가 보낸 인증 정보를 가로채 세션에 저장함으로써, 이후 통신에서 별도의 인증 과정 없이 안전하게 유저를 식별합니다.
```java
if (StompCommand.CONNECT.equals(accessor.getCommand())) {
    String userIdStr = accessor.getFirstNativeHeader("userId"); // 헤더에서 유저 ID 추출
    accessor.getSessionAttributes().put("userId", Long.parseLong(userIdStr)); // 세션 보관
}
```
#### 메시지 라우팅 및 퍼시스턴스 (ChatController)
송신된 메시지를 수신하여 DB에 영구 저장하고, 해당 방을 구독 중인 모든 유저에게 실시간으로 브로드캐스트합니다.
```java
@MessageMapping("/chat/message/{getherId}")
public void sendMessage(@DestinationVariable Long getherId, @Payload ChatSendRequest request, SimpMessageHeaderAccessor headerAccessor) {
  Long userId = (Long) headerAccessor.getSessionAttributes().get("userId"); // 세션에서 유저 식별
  ChatSendResponse response = chatService.saveMessage(getherId, request, userId); // DB 저장
  messagingTemplate.convertAndSend("/sub/chat/room/" + getherId, response); // 구독자에게 전파
}
```

---

# 도메인 : chat

## 이전 채팅 내역 조회
채팅 화면에 들어갔을 때 과거의 채팅 내역을 조회합니다.

### 주요 코드
```java
// ChatMessageRepository.java
@Query("SELECT c FROM ChatMessage c " +
       "JOIN FETCH c.sender " + // N+1 문제 방지를 위한 패치 조인
       "WHERE c.gether.id = :getherId " +
       "AND (:cursor IS NULL OR c.id < :cursor) " + // 커서 기반 조건문
       "ORDER BY c.id DESC")
Slice<ChatMessage> findChatHistory(@Param("getherId") Long getherId, 
                                   @Param("cursor") Long cursor, 
                                   Pageable pageable);
```
* 성능 최적화
  * `c.id < :cursor` 조건을 통해 인덱스를 활용하여 필요한 구간의 데이터만 즉시 조회합니다. 이는 수백만 건의 데이터가 쌓여도 동일한 응답 속도를 보장합니다.

* N+1 문제 해결 (JOIN FETCH)
  * 채팅 메시지를 가져올 때 발신자(User) 정보를 각각 쿼리하지 않고, `JOIN FETCH`를 통해 한 번의 쿼리로 가져와 DB 부하를 최소화했습니다.

* Slice 인터페이스 활용
  * 전체 데이터 개수를 세는 Count 쿼리를 생략하고, "다음 페이지 존재 여부"만 판단하는 `Slice`를 사용하여 무한 스크롤 환경에 최적화된 성능을 구현했습니다.
---
# 배포 (AWS EC2 & RDS)

### 시스템 아키텍처 (System Architecture)
* **Cloud Hosting**: AWS EC2 (Amazon Linux 2023)
* **Domain & DNS**: 가비아(Gavia) 네임서버 설정을 통한 커스텀 도메인(`www.betgether-api.shop`) 운용
* **Web Server**: Nginx (Reverse Proxy)
* **SSL/TLS**: Certbot (Let's Encrypt)을 통한 HTTPS 보안 통신 구축
* **Application**: Spring Boot 3.5.9 (Java 21)

#### 환경 변수를 활용한 민감 정보 보호
DB 주소, 계정 정보, JWT Secret Key 등 민감한 정보를 소스 코드에 포함하지 않고 시스템 환경 변수(`.bashrc`)를 통해 주입했습니다.

### 📊 데이터베이스 설계 (ERD)
![Database ERD](./images/db_erd.png)

