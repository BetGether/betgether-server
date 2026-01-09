package com.betgether.betgether_server.domain.gether.dto.response;

import java.time.LocalDateTime;

public record GetherDetailResponse(
        Long getherId,
        String title,
        String description,
        String imageUrl,
        Boolean isHost,
        String inviteCode,
        Integer participantCount,
        String challengeTitle,
        Integer challengeBetPoint,
        LocalDateTime createdAt
) {
}
