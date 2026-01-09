package com.betgether.betgether_server.domain.chat.handler;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

@Component
public class StompHandler implements ChannelInterceptor {

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        // 1. 처음 연결될 때(CONNECT)만 실행
        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            // 2. 클라이언트가 보낸 nativeHeaders에서 userId 추출
            String userIdStr = accessor.getFirstNativeHeader("userId");

            if (userIdStr != null) {
                try {
                    long userId = Long.parseLong(userIdStr);
                    accessor.getSessionAttributes().put("userId", userId);
                } catch (NumberFormatException e) {
                    System.out.println("[Chat Error] userId 형식이 잘못되었습니다:" + userIdStr);
                }
                // 3. 여기서 넣어줘야 나중에 getSessionAttributes().get("userId")로 꺼낼 수 있음
                accessor.getSessionAttributes().put("userId", Long.parseLong(userIdStr));
                System.out.println("세션에 userId 저장 완료: " + userIdStr);
            } else {
                System.out.println("헤더에 userId가 없습니다.");
            }
        }
        return message;
    }
}