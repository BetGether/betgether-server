package com.betgether.betgether_server.domain.gether.dto.response;

public record GetherDetailResponse(
        Long getherId,
        String title,
        String description,
        String imageUrl,
        Boolean isHost,
        String inviteCode,
        Integer participantCount
) {
}
