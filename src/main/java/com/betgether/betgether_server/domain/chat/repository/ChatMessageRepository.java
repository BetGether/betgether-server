package com.betgether.betgether_server.domain.chat.repository;

import com.betgether.betgether_server.domain.chat.entity.ChatMessage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    @Query("SELECT c FROM ChatMessage c " +
            "JOIN FETCH c.sender " + // N+1 문제 해결을 위한 패치 조인
            "WHERE c.gether.id = :getherId " +
            "AND (:cursor IS NULL OR c.id < :cursor) " + // 커서보다 작은(과거의) ID 조회
            "ORDER BY c.id DESC") // 최신순으로 정렬
    Slice<ChatMessage> findChatHistory(@Param("getherId") Long getherId,
                                       @Param("cursor") Long cursor,
                                       Pageable pageable);
}
