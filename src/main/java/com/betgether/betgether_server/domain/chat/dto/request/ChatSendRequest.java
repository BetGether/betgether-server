package com.betgether.betgether_server.domain.chat.dto.request;

public record ChatSendRequest(
        String content,
        String type
) {
}
