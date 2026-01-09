package com.betgether.betgether_server.domain.verification.service;

import com.betgether.betgether_server.domain.chat.dto.request.ChatSendRequest;
import com.betgether.betgether_server.domain.chat.dto.response.ChatSendResponse;
import com.betgether.betgether_server.domain.chat.entity.ChatType;
import com.betgether.betgether_server.domain.chat.service.ChatService;
import com.betgether.betgether_server.domain.gether.entity.Challenge;
import com.betgether.betgether_server.domain.gether.entity.ChallengeStatus;
import com.betgether.betgether_server.domain.gether.entity.Gether;
import com.betgether.betgether_server.domain.gether.repository.ChallengeRepository;
import com.betgether.betgether_server.domain.gether.repository.GetherRepository;
import com.betgether.betgether_server.domain.gether.repository.ParticipationRepository;
import com.betgether.betgether_server.domain.user.entity.User;
import com.betgether.betgether_server.domain.user.repository.UserRepository;
import com.betgether.betgether_server.domain.verification.dto.response.VerifyScanResponse;
import com.betgether.betgether_server.domain.verification.dto.response.VerifyStartHostResponse;
import com.betgether.betgether_server.domain.verification.entity.PointTransaction;
import com.betgether.betgether_server.domain.verification.entity.PointTransactionType;
import com.betgether.betgether_server.domain.verification.entity.VerificationLog;
import com.betgether.betgether_server.domain.verification.entity.VerificationSession;
import com.betgether.betgether_server.domain.verification.repository.PointTransactionRepository;
import com.betgether.betgether_server.domain.verification.repository.VerificationLogRepository;
import com.betgether.betgether_server.domain.verification.repository.VerificationSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VerifyService {
    private final VerificationLogRepository logRepository;
    private final VerificationSessionRepository sessionRepository;
    private final UserRepository userRepository;
    private final GetherRepository getherRepository;
    private final ParticipationRepository participationRepository;
    private final ChallengeRepository challengeRepository;

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatService chatService;

    private static final int bonusPoint = 50;
    private final PointTransactionRepository pointTransactionRepository;

    @Transactional
    public VerifyStartHostResponse start(Long getherId, Long hostUserId) {
        Gether gether = getherRepository.findById(getherId)
                .orElseThrow(() -> new IllegalArgumentException("게더를 찾을 수 없음."));
        if (!gether.getHost().getId().equals(hostUserId)) {
            throw new IllegalStateException("방장만 인증을 시작할 수 있음.");
        }

        Challenge challenge = challengeRepository
                .findByGether_IdAndStatus(getherId, ChallengeStatus.OPEN)
                .orElseThrow(() -> new IllegalStateException("진행 중인 챌린지가 없습니다."));

        sessionRepository.findFirstByGetherIdAndStatusOrderByCreatedAtDesc(getherId, "ACTIVE")
                .ifPresent(active -> {
                    throw new IllegalStateException("이미 진행중인 인증 세션 존재");
                });

//        int betPoint = challenge.getBetPoint();
        int betPoint = 50; // 배팅금 50 일단 고정

        String token = generateToken();
        LocalDateTime now = LocalDateTime.now();
        // todo : 인증 가능 시간 복구 -> 3분정도?
        LocalDateTime expiredAt = now.plusSeconds(50);


        VerificationSession session = VerificationSession.builder()
                .hostUserId(hostUserId)
                .getherId(getherId)
                .challenge(challenge)
                .token(token)
                .betPoint(betPoint)
                .status("ACTIVE")
                .createdAt(now)
                .expiredAt(expiredAt)
                .build();

        sessionRepository.save(session);
        List<Long> memberIds = participationRepository.findUserIdsByGetherId(getherId);

        if (memberIds.isEmpty()) {
            throw new IllegalStateException("게더 멤버가 없읍니다.");
        }

        List<User> members = userRepository.findAllByIdInForUpdate(memberIds);


        List<PointTransaction> debits = members.stream().map(u -> {
            u.addPoint(-betPoint);
            return PointTransaction.builder()
                    .type(PointTransactionType.BET)
                    .amount(-betPoint)
                    .session(session)
                    .user(u)
                    .build();
        }).collect(Collectors.toList());
        userRepository.saveAll(members);
        pointTransactionRepository.saveAll(debits);


        //TODO : ChatController 호출
        ChatSendResponse response = chatService.saveMessage(getherId, new ChatSendRequest(getherId, "", ChatType.VERIFY_START, hostUserId), hostUserId);
        messagingTemplate.convertAndSend("/sub/chat/room/" + getherId, response);
        return new VerifyStartHostResponse(token, expiredAt);
    }

    @Transactional
    public VerifyScanResponse scan(Long getherId, Long userId, String verifyToken) {
        if (!participationRepository.existsByUser_IdAndGether_Id(userId, getherId))
            throw new IllegalStateException("게더 멤버만 인증할 수 있습니다.");

        VerificationSession session = sessionRepository.findByToken(verifyToken)
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
                .findByGether_IdAndStatus(getherId, ChallengeStatus.OPEN)
                .orElseThrow(() -> new IllegalStateException("진행 중인 챌린지가 없습니다."));

        // 중복 인증 방지 + 로그 저장
        try {
            logRepository.save(
                    VerificationLog.builder()
                            .userId(userId)
                            .sessionId(session.getId())
                            .build()
            );
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("이미 인증을 완료했습니다.");
        }

        long totalMemberCount = participationRepository.countByGether_Id(getherId);
        long successCount = logRepository.countDistinctUserIdBySessionId(session.getId());

        //방장 항상 포함
        if (session.getHostUserId() != null && !logRepository.existsBySessionIdAndUserId(session.getId(), session.getHostUserId())) {
            successCount += 1;
        }
        // 모두 인증 완료 → 세션 바로 정산
        if (successCount >= totalMemberCount) {
            settleSession(session, challenge);
        }

        int currentPoint = userRepository.findById(userId)
                .map(User::getPoint)
                .orElse(0);
        return new VerifyScanResponse(
                "인증 성공!",
                session.getBetPoint(),
                currentPoint
        );
    }

    @Scheduled(fixedDelay = 10000)
    @Transactional
    public void settleExpiredSessions() {
        List<VerificationSession> expiredSessions = sessionRepository.findExpiredActiveSessions(LocalDateTime.now());
        for (VerificationSession session : expiredSessions) {
            Challenge challenge = challengeRepository
                    .findByGether_IdAndStatus(session.getGetherId(), ChallengeStatus.OPEN)
                    .orElseThrow(() -> new IllegalArgumentException("진행중인 챌린지 없음"));
            settleSession(session, challenge);
        }
    }

    private void settleSession(VerificationSession session, Challenge challenge) {
        int updated = sessionRepository.closeIfActive(session.getId());
        if (updated == 0) return; // 이미 다른 트랜잭션이 닫았음 → 중복 정산 방지

        //중복 호출 방지
        if (!session.isActive()) {
            throw new IllegalStateException("이미 종료된 세션");
        }

        session.markClosed();

        int betPoint = session.getBetPoint();
        long totalMemberCount = participationRepository.countByGether_Id(session.getGetherId());

        // scan 한 유저 목록
        List<Long> successUserIds = logRepository.findUser_IdsBySession_Id(session.getId());

        if (successUserIds == null) successUserIds = new ArrayList<>();
        if (session.getHostUserId() != null) successUserIds.add(session.getHostUserId());

        List<Long> winners = successUserIds.stream().distinct().sorted().toList();
        List<User> users = userRepository.findAllByIdInForUpdate(winners);

        long totalPoint = totalMemberCount * betPoint;
        int winnerCount = users.size();

        if (winnerCount == 0) {
            challenge.update(null, null, ChallengeStatus.OPEN);
            return;
        }
        long base = totalPoint / winnerCount;

        boolean allParticipated = (winnerCount == totalMemberCount);
        int bonus = allParticipated ? bonusPoint : 0;
        List<PointTransaction> credits = users.stream().map(u -> {
            long payout = base + bonus;
            u.addPoint((int) payout);
            return PointTransaction.builder()
                    .type(PointTransactionType.SETTLED)
                    .amount((int) payout)
                    .session(session)
                    .user(u)
                    .build();
        }).collect(Collectors.toList());

        userRepository.saveAll(users);
        pointTransactionRepository.saveAll(credits);

        challenge.update(null, null, ChallengeStatus.OPEN);
    }

    private static String generateToken() {
        byte[] bytes = new byte[18];
        new SecureRandom().nextBytes(bytes);
        return "VBT-" + Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
