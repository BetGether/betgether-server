package com.betgether.betgether_server.domain.gether.service;

import com.betgether.betgether_server.domain.gether.dto.response.GetherDetailResponse;
import com.betgether.betgether_server.domain.gether.dto.response.GetherJoinResponse;
import com.betgether.betgether_server.domain.gether.dto.response.GetherSearchResponse;
import com.betgether.betgether_server.domain.gether.dto.response.MyGetherResponse;
import com.betgether.betgether_server.domain.gether.entity.Gether;
import com.betgether.betgether_server.domain.gether.entity.Participation;
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
}
