package com.betgether.betgether_server.domain.gether.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record ChallengeUpdateRequest (
    @NotBlank
    @Size(max = 20)
    String title,
    @Positive
    Integer betPoint
) {
}
