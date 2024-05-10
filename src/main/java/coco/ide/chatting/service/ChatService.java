package coco.ide.chatting.service;

import coco.ide.chatting.Chat;
import coco.ide.chatting.repository.ChatRepository;
import coco.ide.chatting.requestDto.RequestChatDto;
import coco.ide.chatting.responseDto.ResponseChatDto;
import coco.ide.member.domain.Member;
import coco.ide.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.hibernate.query.sqm.tree.SqmNode.log;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;
    private final MemberRepository memberRepository;

    // 메시지 전체 조회하기
//    public List<ResponseChatDto> allMessages() {
//        List<Chat> allResponse = chatRepository.findAll();
//
//        return allResponse.stream().map(chat -> ResponseChatDto.builder()
//                        .message(chat.getMessage())
//                        .member(chat.getMember())
//                        .createdAt(chat.getCreatedAt())
//                        .build())
//                .collect(Collectors.toList());
//    }

    // 메시지 저장하기
    public ResponseChatDto saveMessage(RequestChatDto requestChatDto) {

        log.info("requestChatDto에서 memberId : {}", requestChatDto.getMemberId());

        log.info("requestChatDto에서 message : {}", requestChatDto.getMessage());

        Long memberId = Long.parseLong(requestChatDto.getMemberId());

        Member member = memberRepository.findById(memberId).orElseThrow(() -> new IllegalArgumentException("No member found with ID: " + requestChatDto.getMemberId()));

        ResponseChatDto responseChatDto = ResponseChatDto.builder()
                .message(requestChatDto.getMessage())
                .memberId(memberId)
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
