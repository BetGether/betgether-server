package com.betgether.betgether_server.domain.chat.handler;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;

@Component
public class WebSocketEventListener {

    //private final UserService userService;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String userId = (String) headerAccessor.getSessionAttributes().get("userId");

        // 여기서 직접 DB를 건드리지 않고 Service를 호출하는 것이 좋습니다.
        //userService.userConnect(userId);
        System.out.println("유저 연결 처리 완료");
    }
}