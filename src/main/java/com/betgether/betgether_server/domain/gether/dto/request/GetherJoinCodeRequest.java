package com.betgether.betgether_server.domain.gether.dto.request;

import jakarta.validation.constraints.NotBlank;

public record GetherJoinCodeRequest(
        @NotBlank
        String inviteCode
) {
}
