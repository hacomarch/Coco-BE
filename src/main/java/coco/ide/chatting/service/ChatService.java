package coco.ide.chatting.service;

import coco.ide.chatting.Chat;
import coco.ide.chatting.repository.ChatRepository;
import coco.ide.chatting.requestDto.RequestChatDto;
import coco.ide.chatting.responseDto.ResponseChatDto;
import coco.ide.member.domain.Member;
import coco.ide.member.repository.MemberRepository;
<<<<<<< HEAD
=======
import lombok.NoArgsConstructor;
>>>>>>> e505255ca4bff2b21c6e8988a062225370c65514
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;
    private final MemberRepository memberRepository;

    // 메시지 전체 조회하기
    public List<ResponseChatDto> allMessages() {
        List<Chat> allResponse = chatRepository.findAll();

        return allResponse.stream().map(chat -> ResponseChatDto.builder()
                        .message(chat.getMessage())
                        .member(chat.getMember())
                        .createdAt(chat.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    // 메시지 저장하기
    public ResponseChatDto saveMessage(RequestChatDto requestChatDto) {

        Member member = memberRepository.findById(requestChatDto.getMemberId()).orElseThrow(() -> new IllegalArgumentException("No member found with ID: " + requestChatDto.getMemberId()));

        ResponseChatDto responseChatDto = ResponseChatDto.builder()
                .message(requestChatDto.getMessage())
                .member(member)
                .createdAt(LocalDateTime.now())
                .build();

        Chat chat = Chat.builder()
                .message(requestChatDto.getMessage())
                .member(member)
                .createdAt(LocalDateTime.now())
                .build();

        chatRepository.save(chat);

        return responseChatDto;
    }
}
