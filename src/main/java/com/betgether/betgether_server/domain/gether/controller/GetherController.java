package com.betgether.betgether_server.domain.gether.controller;

import com.betgether.betgether_server.domain.gether.dto.request.GetherCreateRequest;
import com.betgether.betgether_server.domain.gether.dto.request.GetherJoinCodeRequest;
import com.betgether.betgether_server.domain.gether.dto.request.GetherUpdateRequest;
import com.betgether.betgether_server.domain.gether.dto.response.*;
import com.betgether.betgether_server.domain.gether.service.GetherService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/gethers")
@RequiredArgsConstructor
public class GetherController {
    private final GetherService getherService;

    @GetMapping("/my")
    public List<MyGetherResponse> getMyGethers(@RequestAttribute("userId") Long userId) {
        return getherService.getMyGethers(userId);
    }

    @GetMapping
    public List<GetherSearchResponse> searchResponses(
            @RequestAttribute("userId") Long userId,
            @RequestParam(required = false) String keyword
    ) {
        return getherService.search(keyword);
    }

    @GetMapping("/{getherId}")
    public GetherDetailResponse getGetherDetail(
            @RequestAttribute("userId") Long userId,
            @PathVariable Long getherId
    ) {
        return getherService.getDetail(userId, getherId);
    }

    @PostMapping("/{getherId}/join")
    public GetherJoinResponse joinGether(
            @RequestAttribute("userId") Long userId,
            @PathVariable Long getherId
    ) {
        return getherService.join(userId, getherId);
    }

    @PostMapping
    public GetherCreateResponse create (
            @RequestAttribute("userId") Long userId,
            @RequestBody @Valid GetherCreateRequest request
            ) {
        return getherService.create(userId, request);
    }

    @PatchMapping("/{getherId}")
    public GetherUpdateResponse update(
            @RequestAttribute("userId") Long userId,
            @PathVariable Long getherId,
            @RequestBody @Valid GetherUpdateRequest request
            ) {
        return getherService.update(userId, getherId, request);
    }

    @PostMapping("/join")
    public GetherJoinResponse joinWithCode(
            @RequestAttribute("userId") Long userId,
            @RequestBody @Valid GetherJoinCodeRequest request
    ) {
        return getherService.joinByInviteCode(userId, request);
    }

}
