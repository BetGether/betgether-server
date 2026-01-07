package com.betgether.betgether_server.domain.chat.dto.response;

import java.util.List;

public record ChatHistoryPageResponse(
        List<ChatHistoryResponse> items,
        Long nextCursor,
        boolean hasNext
) {
}
