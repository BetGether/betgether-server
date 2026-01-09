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
        // 1. 우선 Object로 꺼냅니다. (타입이 뭔지 모르니까요)
        Object userIdObj = headerAccessor.getSessionAttributes().get("userId");

        // 2. String.valueOf()를 사용하면 Long이든 String이든 안전하게 문자열이 됩니다.
        if (userIdObj != null) {
            String userId = String.valueOf(userIdObj);
            System.out.println("User Connected: " + userId);
        }

        // 여기서 직접 DB를 건드리지 않고 Service를 호출하는 것이 좋습니다.
        //userService.userConnect(userId);

    }
}