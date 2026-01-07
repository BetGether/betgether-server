package com.betgether.betgether_server.domain.gether.dto.response;

import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public record MyGetherResponse(
        @Positive(message = "getherId는 양수여야 한다")
        Long getherId,
        String title,
        String imageUrl,
        Integer participantCount,
        LocalDate joinedAt
) {
}
