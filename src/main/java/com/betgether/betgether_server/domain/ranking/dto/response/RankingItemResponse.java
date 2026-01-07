package com.betgether.betgether_server.domain.ranking.dto.response;

public record RankingItemResponse(
        Integer rank,
        String nickname,
        Integer point
) {
}
