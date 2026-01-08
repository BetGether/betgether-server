package com.betgether.betgether_server.domain.gether.dto.request;

import jakarta.validation.Valid;

public record GetherUpdateRequest (
    String title,
    String description,
    String imageUrl,
    Boolean isPublic,
    @Valid
    ChallengeUpdateRequest challenge
) {
}
