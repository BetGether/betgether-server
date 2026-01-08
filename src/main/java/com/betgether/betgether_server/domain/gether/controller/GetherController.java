package com.betgether.betgether_server.domain.gether.controller;

import com.betgether.betgether_server.domain.gether.dto.response.GetherDetailResponse;
import com.betgether.betgether_server.domain.gether.dto.response.GetherJoinResponse;
import com.betgether.betgether_server.domain.gether.dto.response.GetherSearchResponse;
import com.betgether.betgether_server.domain.gether.dto.response.MyGetherResponse;
import com.betgether.betgether_server.domain.gether.service.GetherService;
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
}
