package com.betgether.betgether_server.domain.verification.dto.response;

public record VerifyScanResponse(
        String message,
        Integer earnedPoint,
        Integer totalPoint
) {
}
