## 도메인 : chat

### 이전 채팅 내역 조회
채팅 화면에 들어갔을 때 과거의 채팅 내역을 조회합니다.

```java
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

  * c.id < :cursor 조건을 통해 인덱스를 활용하여 필요한 구간의 데이터만 즉시 조회합니다. 이는 수백만 건의 데이터가 쌓여도 동일한 응답 속도를 보장합니다.

* N+1 문제 해결 (JOIN FETCH)

  * 채팅 메시지를 가져올 때 발신자(User) 정보를 각각 쿼리하지 않고, JOIN FETCH를 통해 한 번의 쿼리로 가져와 DB 부하를 최소화했습니다.

* Slice 인터페이스 활용

  * 전체 데이터 개수를 세는 Count 쿼리를 생략하고, "다음 페이지 존재 여부"만 판단하는 Slice를 사용하여 무한 스크롤 환경에 최적화된 성능을 구현했습니다.
