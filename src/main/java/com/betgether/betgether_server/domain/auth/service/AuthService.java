package com.betgether.betgether_server.domain.auth.service;

import com.betgether.betgether_server.domain.auth.dto.request.LoginRequest;
import com.betgether.betgether_server.domain.auth.dto.response.LoginResponse;
import com.betgether.betgether_server.domain.user.entity.User;
import com.betgether.betgether_server.domain.user.respository.UserRepository;
import com.betgether.betgether_server.global.jwt.JwtProvider;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static java.time.LocalDateTime.*;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;

    //로그인 -> 닉네임 없으면 생성, 있으면 있는거 반환
    @Transactional
    public LoginResponse login(LoginRequest request) {
        String nickname = request.nickname();
        User user = userRepository.findByNickname(nickname)
                .orElseGet(()-> userRepository.save(
                                    User.builder()
                                            .nickname(nickname)
                                            .point(500)
                                            .lastLogin(now())
                                            .build()));
        if (user.isFirstLoginToday(now())) {
            user.addPoint(50);
        }
        user.updateLastLogin(now());
        String accessToken = jwtProvider.generateAccessToken(user.getId(), nickname);
        return LoginResponse.from(user, accessToken);
    }
}
