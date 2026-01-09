package com.betgether.betgether_server.domain.verification.service;

import com.betgether.betgether_server.domain.gether.repository.ParticipationRepository;
import com.betgether.betgether_server.domain.user.entity.User;
import com.betgether.betgether_server.domain.user.repository.UserRepository;
import com.betgether.betgether_server.domain.verification.dto.response.VerifyConfirmResponse;
import com.betgether.betgether_server.domain.verification.repository.PointTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class VerifyConfirmService {
    private final UserRepository userRepository;
    private final PointTransactionRepository pointTransactionRepository;
    private final ParticipationRepository participationRepository;

    @Transactional
    public VerifyConfirmResponse confirm(Long getherId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new IllegalArgumentException("해당 유저가 없음."));
        String nickname = user.getNickname();
        int totalPoint = user.getPoint();

        // 요청 유저의 "가장 최근" 포인트 변화 기록(획득이면 +, 손실이면 -)
        var latestTxOpt = pointTransactionRepository.findTopByUser_IdOrderByCreatedAtDesc(userId);
        int earnedPoint = latestTxOpt.map(pt -> pt.getAmount() == null ? 0 : pt.getAmount()).orElse(0);
        long totalGetherNumber = participationRepository.countByGether_Id(getherId);

        // totalParticipation: (최근 포인트 변화가 발생한) 세션에 참여한 유저 수
        // 세션이 없거나 최신 트랜잭션이 없으면 0
        long totalParticipation = latestTxOpt
                .map(pt -> pt.getSession() == null ? null : pt.getSession().getId())
                .map(pointTransactionRepository::countDistinctUsersBySessionId)
                .orElse(0L);

        return new VerifyConfirmResponse(nickname, earnedPoint, totalPoint, totalGetherNumber, totalParticipation);
    }
}
