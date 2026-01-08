package com.betgether.betgether_server.domain.auth.dto.response;

import com.betgether.betgether_server.domain.user.entity.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LoginResponse(
    @NotNull(message = "id는 null이 아님")
    Long userId,
    @NotBlank(message = "이름은 한 글자 이상, 공백 X")
    String nickname,
    String accessToken
) {
    public static LoginResponse from(User user, String accessToken) {
        return new LoginResponse(user.getId(), user.getNickname(), accessToken);
    }
}
