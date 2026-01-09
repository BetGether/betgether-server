package com.betgether.betgether_server.domain.verification.dto.response;

public record VerifyConfirmResponse(
        String nickname,
        Integer earnedPoint,
        Integer totalPoint,
        Long totalGethers,
        Long totalParticipation
) {
}
