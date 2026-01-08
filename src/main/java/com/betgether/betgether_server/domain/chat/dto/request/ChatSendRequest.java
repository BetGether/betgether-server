package com.betgether.betgether_server.domain.chat.dto.request;

import com.betgether.betgether_server.domain.chat.entity.ChatType;

public record ChatSendRequest(
        Long getherId,
        String content,
        ChatType type,
        Long userId
) {
}
