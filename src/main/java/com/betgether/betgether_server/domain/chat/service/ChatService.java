package com.betgether.betgether_server.domain.chat.service;

import com.betgether.betgether_server.domain.chat.dto.request.ChatSendRequest;
import com.betgether.betgether_server.domain.chat.dto.response.ChatHistoryPageResponse;
import com.betgether.betgether_server.domain.chat.dto.response.ChatHistoryResponse;
import com.betgether.betgether_server.domain.chat.dto.response.ChatSendResponse;
import com.betgether.betgether_server.domain.chat.entity.ChatMessage;
import com.betgether.betgether_server.domain.chat.repository.ChatMessageRepository;
import com.betgether.betgether_server.domain.gether.entity.Gether;
import com.betgether.betgether_server.domain.gether.repository.GetherRepository;
import com.betgether.betgether_server.domain.user.entity.User;
import com.betgether.betgether_server.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

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

    @Transactional
    public ChatHistoryPageResponse getChatHistory(Long getherId, Long cursor, int size) {
        // 1. 페이징 정보 생성 (최신순으로 가져오기 위해 정렬은 이미 Repository 쿼리에 포함됨)
        Pageable pageable = PageRequest.of(0, size);

        // 2. Repository 호출 (Slice 형태로 데이터를 가져옴)
        Slice<ChatMessage> chatMessages = chatMessageRepository.findChatHistory(getherId, cursor, pageable);

        // 3. Entity -> DTO 변환
        List<ChatHistoryResponse> items = chatMessages.getContent().stream()
                .map(message -> new ChatHistoryResponse(
                        message.getId(),
                        message.getSender().getNickname(),
                        message.getContent(),
                        message.getType().name(),
                        message.getCreatedAt()
                ))
                .toList();

        // 4. 다음 페이지를 위한 커서 계산 및 다음 페이지 존재 여부 확인
        Long nextCursor = null;
        boolean hasNext = chatMessages.hasNext();

        if (hasNext && !items.isEmpty()) {
            // [Insight] 마지막 아이템의 ID가 다음 요청의 커서가 됩니다.
            nextCursor = items.get(items.size() - 1).messageId();
        }

        return new ChatHistoryPageResponse(items, nextCursor, hasNext);
    }

}
