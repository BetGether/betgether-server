package com.betgether.betgether_server.domain.auth.controller;

import com.betgether.betgether_server.global.jwt.JwtProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {
    private final JwtProvider jwtProvider;

    public AuthInterceptor(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 1. Preflight(OPTIONS) 요청은 인증 절차 없이 통과
        if (request.getMethod().equals("OPTIONS")) {
            return true;
        }

        String auth = request.getHeader("Authorization");
        if (auth == null || !auth.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "인증 토큰 없거나 형식이 안 맞음");
        }

        String token = auth.substring(7);
        long userId = jwtProvider.getUserId(token);
        request.setAttribute("userId", userId);

        return true;
    }
}
