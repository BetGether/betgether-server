package com.betgether.betgether_server.domain.chat.dto.response;

import com.betgether.betgether_server.domain.chat.entity.ChatMessage;
import com.betgether.betgether_server.domain.chat.entity.ChatType;

import java.time.LocalDateTime;

public record ChatSendResponse(
        Long messageId,
        String senderNickname,
        String content,
        ChatType type,
        LocalDateTime createdAt
) {
    public static ChatSendResponse from(ChatMessage chatMessage) {
        return new ChatSendResponse(
                chatMessage.getId(),
                chatMessage.getSender().getNickname(),
                chatMessage.getContent(),
                chatMessage.getType(),
                chatMessage.getCreatedAt()
        );
    }
}
