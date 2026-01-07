package com.betgether.betgether_server.domain.auth.dto.response;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LoginResponse(
    @NotNull(message = "id는 null이 아님")
    Long userId,
    @NotBlank(message = "이름은 한 글자 이상, 공백 X")
    String nickname,
    String accessToken
) {
}
