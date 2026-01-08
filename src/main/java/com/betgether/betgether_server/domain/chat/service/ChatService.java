package com.betgether.betgether_server.domain.chat.service;

import com.betgether.betgether_server.domain.chat.dto.request.ChatSendRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service // Bean 등록을 위해 추가
@RequiredArgsConstructor // final이 붙은 필드로 생성자를 자동 생성
public class ChatService {

    private final SimpMessagingTemplate messagingTemplate;

    public void broadcastMessage(ChatSendRequest message) {
        // 4. 유저가 속한 방(/sub/chat/room/{roomId})으로 브로드캐스트
        // 이 주소를 구독하고 있는 모든 유저에게 메시지가 전달됩니다.
        messagingTemplate.convertAndSend("/sub/chat/gether/" + message.getherId(), message);
        System.out.println("BROADCAST CHAT : " + message);

        //@TODO: 데이터베이스 저장 로직 구현
    }
}
