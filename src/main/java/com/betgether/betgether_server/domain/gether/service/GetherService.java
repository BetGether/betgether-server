package com.betgether.betgether_server.domain.gether.service;

import com.betgether.betgether_server.domain.gether.dto.request.GetherCreateRequest;
import com.betgether.betgether_server.domain.gether.dto.request.GetherJoinCodeRequest;
import com.betgether.betgether_server.domain.gether.dto.request.GetherUpdateRequest;
import com.betgether.betgether_server.domain.gether.dto.response.*;
import com.betgether.betgether_server.domain.gether.entity.Challenge;
import com.betgether.betgether_server.domain.gether.entity.Gether;
import com.betgether.betgether_server.domain.gether.entity.Participation;
import com.betgether.betgether_server.domain.gether.repository.ChallengeRepository;
import com.betgether.betgether_server.domain.gether.repository.GetherRepository;
import com.betgether.betgether_server.domain.gether.repository.ParticipationRepository;
import com.betgether.betgether_server.domain.user.entity.User;
import com.betgether.betgether_server.domain.user.respository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetherService {
    private final ParticipationRepository participationRepository;
    private final GetherRepository getherRepository;
    private final UserRepository userRepository;
    private final ChallengeRepository challengeRepository;

    @Transactional(readOnly = true)
    public List<MyGetherResponse> getMyGethers(Long userId) {
        return participationRepository.findMyGethers(userId);
    }

    @Transactional(readOnly = true)
    public List<GetherSearchResponse> search(String keyword) {
        return getherRepository.search(keyword);
    }

    @Transactional(readOnly = true)
    public GetherDetailResponse getDetail(Long userId, Long getherId) {
        return getherRepository.findDetail(getherId, userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게더가 존재하지 않음. getherId = " + getherId));
    }

    @Transactional
    public GetherJoinResponse join(Long userId, Long getherId) {
        //가입 확인
        if (participationRepository.existsByUser_IdAndGether_Id(userId, getherId)) {
            return new GetherJoinResponse("ALREADY_JOINED", getherId);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저 없음 userId : " + userId));
        Gether gether = getherRepository.findById(getherId)
                .orElseThrow(() -> new IllegalArgumentException("게더 없음 getherId : " + getherId));

        try {
            Participation participation = Participation.builder().user(user).gether(gether).build();
            participationRepository.save(participation);
            return new GetherJoinResponse("JOINED", getherId);
        } catch (DataIntegrityViolationException e) { // 동시성으로 이미 가입 처리
            return new GetherJoinResponse("ALREADY_JOINED", getherId);
        }
    }

    @Transactional
    public GetherCreateResponse create(Long userId, GetherCreateRequest req) {
        User host = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("유저 없음 userId : " + userId));

        Gether gether = Gether.builder()
                .host(host)
                .title(req.title())
                .description(req.description())
                .imageUrl(req.imageUrl())
                .isPublic(req.isPublic() == null || req.isPublic())
                .inviteCode(generateInviteCode()).build();

        getherRepository.save(gether);
        try {
            participationRepository.save(
                    Participation.builder().user(host).gether(gether).build()); //호스트 추가
        } catch (DataIntegrityViolationException ignored) { // 동시성 문제
        }

        if (req.challenge() == null) {
            throw new IllegalArgumentException("challenge는 필수입니다.");
        }
        if (challengeRepository.existsByGether_Id(gether.getId())) {
            throw new IllegalStateException("이미 챌린지가 존재합니다.");
        }

        Challenge ch = Challenge.builder()
                .gether(gether)
                .title(req.challenge().title())
                .betPoint(req.challenge().betPoint())
                .status(Challenge.Status.CLOSED)
                .build();

        challengeRepository.save(ch);
        gether.setChallenge(ch);

        return new GetherCreateResponse(gether.getId(), "CREATED");
    }

    private String generateInviteCode() {
        return java.util.UUID.randomUUID().toString().replace("-", "").substring(0, 8);
    }

    @Transactional
    public GetherUpdateResponse update(Long userId, Long getherId, GetherUpdateRequest req) {
        Gether gether = getherRepository.findById(getherId)
                .orElseThrow(() -> new IllegalArgumentException("게더가 없음 getherId: " + getherId));
        if (!gether.getHost().getId().equals(getherId)) {
            throw new IllegalStateException("호스트만 게더 수정 가능");
        }

        gether.update(req.title(), req.description(), req.imageUrl(), req.isPublic());

        if (req.challenge() != null) { // challenge 안 넘머올 수도 있다고 가정함..
            Challenge challenge = challengeRepository.findByGether_Id(getherId)
                    .orElseThrow(() -> new IllegalArgumentException("해당 게더의 챌린지 존재 안함"));
            challenge.update(req.challenge().title(), req.challenge().betPoint(), null);
        }

        return new GetherUpdateResponse(getherId, "UPDATED");
    }

    @Transactional
    public GetherJoinResponse joinByInviteCode(Long userId, GetherJoinCodeRequest request) {
        Gether gether = getherRepository.findByInviteCode(request.inviteCode())
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 초대 코드입니다."));

        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("해당 유저는 없음 userId : " + userId));
        try {
            participationRepository.save(
                    Participation.builder().user(user).gether(gether).build()
            );
            return new GetherJoinResponse("JOIN_SUCCESS", gether.getId());
        } catch (DataIntegrityViolationException e) {
            return new GetherJoinResponse("ALREADY_JOIN", gether.getId());
        }
    }
}
