package com.betgether.betgether_server.domain.auth.controller;

import com.betgether.betgether_server.domain.auth.service.AuthService;
import com.betgether.betgether_server.domain.auth.dto.request.LoginRequest;
import com.betgether.betgether_server.domain.auth.dto.response.LoginResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }
}
