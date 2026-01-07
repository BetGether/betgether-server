package com.betgether.betgether_server.domain.chat.dto.response;

import java.time.LocalDateTime;

public record ChatHistoryResponse(
        Long messageId,
        String senderNickname,
        String content,
        String type,
        LocalDateTime createdAt
) {
}
