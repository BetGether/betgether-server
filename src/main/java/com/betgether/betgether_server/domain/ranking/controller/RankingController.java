package com.betgether.betgether_server.domain.ranking.controller;

import com.betgether.betgether_server.domain.ranking.dto.response.RankingResponse;
import com.betgether.betgether_server.domain.ranking.service.RankingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ranking")
public class RankingController {
    private final RankingService rankingService;

    @GetMapping
    public RankingResponse getRanking(@RequestAttribute Long userId){
        return rankingService.getRanking(userId);
    }
}
