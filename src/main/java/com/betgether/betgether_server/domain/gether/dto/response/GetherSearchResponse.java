package com.betgether.betgether_server.domain.gether.dto.response;

import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;

public record GetherSearchResponse(
        @Positive
        Long getherId,
        String title,
        String description,
        String imageUrl,
        Integer participantCount,
        LocalDateTime createdAt,
        String challengeTitle
) {
}
