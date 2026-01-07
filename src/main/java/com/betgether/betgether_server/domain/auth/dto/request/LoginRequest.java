package com.betgether.betgether_server.domain.auth.dto.request;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "이름은 한 글자 이상, 공백 X")
        String nickname
) {
}
