package com.betgether.betgether_server.domain.verification.controller;

import com.betgether.betgether_server.domain.verification.dto.request.VerifyScanRequest;
import com.betgether.betgether_server.domain.verification.dto.response.VerifyScanResponse;
import com.betgether.betgether_server.domain.verification.dto.response.VerifyStartHostResponse;
import com.betgether.betgether_server.domain.verification.service.VerifyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/gethers/{getherId}/verify")
@RequiredArgsConstructor
public class VerifyController {
    private final VerifyService verifyService;

    @PostMapping
    public VerifyStartHostResponse start(
            @PathVariable Long getherId,
            @RequestAttribute("userId") Long userId
    ) {
        return verifyService.start(getherId, userId);
    }

    @PostMapping("/scan")
    public VerifyScanResponse scan (
            @PathVariable Long getherId,
            @RequestAttribute("userId") Long userId,
            @RequestBody VerifyScanRequest request
            ) {
        return verifyService.scan(getherId, userId, request.verifyToken());
    }
}
