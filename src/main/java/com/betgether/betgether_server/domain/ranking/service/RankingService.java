package com.betgether.betgether_server.domain.ranking.service;

import com.betgether.betgether_server.domain.ranking.RankingItemView;
import com.betgether.betgether_server.domain.ranking.dto.response.RankingItemResponse;
import com.betgether.betgether_server.domain.ranking.dto.response.RankingResponse;
import com.betgether.betgether_server.domain.user.respository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RankingService {
    private final UserRepository userRepository;

    public RankingResponse getRanking(Long userId) {
        List<RankingItemResponse> all = userRepository.findTopRanking(20).stream()
                .map(v -> new RankingItemResponse(v.getRank(), v.getNickName(), v.getPoint()))
                .toList();

        List<RankingItemResponse> top3 = all.stream().limit(3).toList();

        RankingItemView myView = userRepository.findMyRanking(userId);
        RankingItemResponse my = (myView == null) ? null : new RankingItemResponse(myView.getRank(), myView.getNickName(), myView.getPoint());

        return new RankingResponse(top3, all, my);
    }
}
