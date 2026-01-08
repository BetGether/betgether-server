package com.betgether.betgether_server.domain.verification.service;

import com.betgether.betgether_server.domain.gether.entity.Challenge;
import com.betgether.betgether_server.domain.gether.entity.Gether;
import com.betgether.betgether_server.domain.gether.repository.ChallengeRepository;
import com.betgether.betgether_server.domain.gether.repository.GetherRepository;
import com.betgether.betgether_server.domain.gether.repository.ParticipationRepository;
import com.betgether.betgether_server.domain.user.entity.User;
import com.betgether.betgether_server.domain.user.respository.UserRepository;
import com.betgether.betgether_server.domain.verification.dto.response.VerifyScanResponse;
import com.betgether.betgether_server.domain.verification.dto.response.VerifyStartHostResponse;
import com.betgether.betgether_server.domain.verification.entity.VerificationLog;
import com.betgether.betgether_server.domain.verification.entity.VerifySession;
import com.betgether.betgether_server.domain.verification.repository.VerificationLogRepository;
import com.betgether.betgether_server.domain.verification.repository.VerificationSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class VerifyService {
    private final VerificationLogRepository logRepository;
    private final VerificationSessionRepository sessionRepository;
    private final UserRepository userRepository;
    private final GetherRepository getherRepository;
    private final ParticipationRepository participationRepository;
    private final ChallengeRepository challengeRepository;

    @Transactional
    public VerifyStartHostResponse start(Long getherId, Long hostUserId) {
        Gether gether = getherRepository.findById(getherId)
                .orElseThrow(() -> new IllegalArgumentException("게더를 찾을 수 없음."));
        if (!gether.getHost().getId().equals(hostUserId)) {
            throw new IllegalStateException("방장만 인증을 시작할 수 있음.");
        }

        // OPEN 상태의 챌린지 조회
        Challenge challenge = challengeRepository
                .findByGether_IdAndStatus(getherId, Challenge.Status.OPEN)
                .orElseThrow(() -> new IllegalStateException("진행 중인 챌린지가 없습니다."));

        int betPoint = challenge.getBetPoint();

        String token = generateToken();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiredAt = now.plusMinutes(3);

        VerifySession session = new VerifySession(hostUserId, getherId, token, betPoint, now, expiredAt);
        sessionRepository.save(session);

        return new VerifyStartHostResponse(token, expiredAt);
    }

    @Transactional
    public VerifyScanResponse scan(Long getherId, Long userId, String verifyToken) {
        if (!participationRepository.existsByUser_IdAndGether_Id(userId, getherId))
            throw new IllegalStateException("게더 멤버만 인증할 수 있습니다.");

        VerifySession session = sessionRepository.findByToken(verifyToken)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 토큰입니다."));

        if (!session.getGetherId().equals(getherId)) {
            throw new IllegalArgumentException("게더 정보가 일치하지 않습니다.");
        }

        if (!session.isActive()) {
            throw new IllegalStateException("이미 종료된 인증입니다.");
        }

        if (session.isExpired(LocalDateTime.now())) {
            session.markExpired();
            throw new IllegalStateException("인증 시간이 만료되었습니다.");
        }

        // OPEN 챌린지 조회
        Challenge challenge = challengeRepository
                .findByGether_IdAndStatus(getherId, Challenge.Status.OPEN)
                .orElseThrow(() -> new IllegalStateException("진행 중인 챌린지가 없습니다."));

        // 중복 인증 방지 + 로그 저장
        try {
            logRepository.save(
                    new VerificationLog(userId, session.getId(), challenge.getBetPoint())
            );
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("이미 인증을 완료했습니다.");
        }


        // 인증 인원 증가
        challenge.increaseInCount();

        // 전체 게더 인원 수 조회
        long totalMemberCount = participationRepository.countByGether_Id(getherId);

        // 모두 인증 완료 → 챌린지 종료
        if (challenge.getInCount() >= totalMemberCount) {
            challenge.update(null, null, Challenge.Status.CLOSED);
        }

        return new VerifyScanResponse(
                "인증 성공!",
                challenge.getBetPoint(), 1300
        );
    }

    private static String generateToken() {
        byte[] bytes = new byte[18];
        new SecureRandom().nextBytes(bytes);
        return "VBT-" + Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
