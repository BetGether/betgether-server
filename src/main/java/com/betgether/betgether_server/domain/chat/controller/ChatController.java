package com.betgether.betgether_server.domain.chat.controller;

import com.betgether.betgether_server.domain.chat.dto.request.ChatSendRequest;
import com.betgether.betgether_server.domain.chat.dto.response.ChatSendResponse;
import com.betgether.betgether_server.domain.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatService chatService; // DB 저장을 담당하는 서비스

    @MessageMapping("/chat/message/{getherId}")
    public void sendMessage(@DestinationVariable Long getherId,
                            @Payload ChatSendRequest request,
                            SimpMessageHeaderAccessor headerAccessor) {

        // 1. JWT 등에서 추출한 유저 ID 가져오기 (인증 파트너와 상의 필요)
        Long userId = (Long) headerAccessor.getSessionAttributes().get("userId");
//        Long userId = 1L;

        // 2. DB에 저장
        ChatSendResponse response = chatService.saveMessage(getherId, request, userId);

        // 3. /sub/chat/room/{getherId}를 구독 중인 사람들에게 메시지 전달
        messagingTemplate.convertAndSend("/sub/chat/room/" + getherId, response);
    }
}