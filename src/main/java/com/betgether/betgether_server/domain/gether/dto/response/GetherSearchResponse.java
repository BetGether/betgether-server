package com.betgether.betgether_server.domain.gether.dto.response;

import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public record GetherSearchResponse(
        @Positive
        Long getherId,
        String title,
        String description,
        String imageUrl,
        Integer participantCount,
        LocalDate createdAt
) {
}
