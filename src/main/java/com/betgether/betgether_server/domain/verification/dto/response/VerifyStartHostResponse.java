package com.betgether.betgether_server.domain.verification.dto.response;

import java.time.LocalDateTime;

public record VerifyStartHostResponse(
        String verifyToken,
        LocalDateTime expiredAt
) {
}
