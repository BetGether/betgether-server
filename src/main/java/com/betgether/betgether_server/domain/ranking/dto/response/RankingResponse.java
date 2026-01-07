package com.betgether.betgether_server.domain.ranking.dto.response;

import java.util.List;

public record RankingResponse (
    List<RankingItemResponse> top3,
    List<RankingItemResponse> allRanking,
    RankingItemResponse myRanking
) {
}