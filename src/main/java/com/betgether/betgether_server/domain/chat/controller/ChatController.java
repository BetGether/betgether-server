package com.betgether.betgether_server.domain.chat.controller;

import com.betgether.betgether_server.domain.chat.dto.request.ChatSendRequest;
import com.betgether.betgether_server.domain.chat.dto.response.ChatSendResponse;
import com.betgether.betgether_server.domain.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatService chatService; // DB 저장을 담당하는 서비스

    @MessageMapping("/chat/message") // 유저가 /pub/chat/message로 전송
    public void messageFromUser(ChatSendRequest message) {
        // 3. 서버는 채팅을 데이터베이스에 저장한다
        chatService.broadcastMessage(message);
    }
}