package com.betgether.betgether_server.domain.gether.dto.request;

import com.betgether.betgether_server.domain.gether.entity.Challenge;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record GetherCreateRequest(
        @NotBlank
        @Size(max = 100)
        String title,
        @NotBlank
        String description,
        String imageUrl,
        Boolean isPublic,
        @NotNull
        ChallengeCreateRequest challenge
) {
}
