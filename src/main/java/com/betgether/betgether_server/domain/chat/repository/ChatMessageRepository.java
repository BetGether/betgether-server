package com.betgether.betgether_server.domain.chat.repository;

import com.betgether.betgether_server.domain.chat.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByGetherIdOrderByCreatedAtAsc(Long getherId);
    List<ChatMessage> findByGetherIdOrderByCreatedAtDesc(Long getherId); // 최신순
}
