package com.betgether.betgether_server.domain.chat.service;

import com.betgether.betgether_server.domain.chat.dto.request.ChatSendRequest;
import com.betgether.betgether_server.domain.chat.dto.response.ChatSendResponse;
import com.betgether.betgether_server.domain.chat.entity.ChatMessage;
import com.betgether.betgether_server.domain.chat.repository.ChatMessageRepository;
import com.betgether.betgether_server.domain.gether.entity.Gether;
import com.betgether.betgether_server.domain.gether.repository.GetherRepository;
import com.betgether.betgether_server.domain.user.entity.User;
import com.betgether.betgether_server.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service // Bean 등록을 위해 추가
@RequiredArgsConstructor // final이 붙은 필드로 생성자를 자동 생성
public class ChatService {
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;
    private final GetherRepository getherRepository;

    @Transactional
    public ChatSendResponse saveMessage(Long getherId, ChatSendRequest request, Long senderId) {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));
        Gether gether = getherRepository.findById(getherId)
                .orElseThrow(() -> new RuntimeException("방을 찾을 수 없습니다."));

        ChatMessage chatMessage = ChatMessage.builder()
                .gether(gether)
                .sender(sender)
                .content(request.content())
                .type(request.type())
                .build();

        chatMessageRepository.save(chatMessage);

        return ChatSendResponse.from(chatMessage); // 응답 DTO로 변환
    }

    private final SimpMessagingTemplate messagingTemplate;

    public void broadcastMessage(ChatSendRequest message) {
        // 4. 유저가 속한 방(/sub/chat/room/{roomId})으로 브로드캐스트
        // 이 주소를 구독하고 있는 모든 유저에게 메시지가 전달됩니다.
        messagingTemplate.convertAndSend("/sub/chat/gether/" + message.getherId(), message);
        System.out.println("BROADCAST CHAT : " + message);

        //@TODO: 데이터베이스 저장 로직 구현
    }
}
