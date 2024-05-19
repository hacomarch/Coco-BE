package coco.ide.chatting.service;

import coco.ide.chatting.Chat;
import coco.ide.chatting.repository.ChatRepository;
import coco.ide.chatting.requestDto.RequestChatDto;
import coco.ide.chatting.responseDto.ResponseChatDto;
import coco.ide.member.domain.Member;
import coco.ide.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Array;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.hibernate.query.sqm.tree.SqmNode.log;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;
    private final MemberRepository memberRepository;

    // 모든 메시지 가져오기
    public List<ResponseChatDto> allMessage() {
        List<Chat> allMessageList = chatRepository.findAll();

        List<ResponseChatDto> messageList = new ArrayList<>();

        for (Chat message : allMessageList) {
            ResponseChatDto responseChatDto = ResponseChatDto.builder()
                    .chatId(message.getChatId())
                    .message(message.getMessage())
                    .memberId(message.getMember().getMemberId())
                    .nickname(message.getMember().getNickname())
                    .isDeleted(message.getIsDeleted())
                    .createdAt(message.getCreatedAt())
                    .build();

            messageList.add(responseChatDto);
        }

        return messageList;
    }

    // 메시지 저장하기
    public ResponseChatDto saveMessage(RequestChatDto requestChatDto) {

        log.info("requestChatDto에서 memberId : {}", requestChatDto.getMemberId());

        log.info("requestChatDto에서 message : {}", requestChatDto.getMessage());

        Long memberId = Long.parseLong(requestChatDto.getMemberId());

        Member member = memberRepository.findById(memberId).orElseThrow(()
                -> new IllegalArgumentException("No member found with ID: " + requestChatDto.getMemberId()));

        ResponseChatDto responseChatDto = ResponseChatDto.builder()
                .message(requestChatDto.getMessage())
                .memberId(memberId)
                .nickname(member.getNickname())
                .isDeleted(false)
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

    // 메시지 조회하기
    public List<ResponseChatDto> searchMessage(String word) {
        List<Chat> searchMessageList = chatRepository.findMessagesContaining(word);

        List<ResponseChatDto> wordContainList = new ArrayList<>();

        for (Chat c : searchMessageList) {
            ResponseChatDto responseChatDto = ResponseChatDto.builder()
                    .message(c.getMessage())
                    .isDeleted(c.getIsDeleted())
                    .memberId(c.getMember().getMemberId())
                    .nickname(c.getMember().getNickname())
                    .isDeleted(c.getIsDeleted())
                    .build();

            wordContainList.add(responseChatDto);
        }
        return wordContainList;
    }

    // 메시지 삭제하기
    public void deleteMessage(Long messageId) {
        Optional<Chat> findMessage = chatRepository.findById(messageId);

        // 만약 해당 메시지의 아이디가 존재한다면
        if (findMessage.isPresent()) {
            Chat chat = findMessage.get();
            chat.setDeleted(true);
            chatRepository.save(chat);
            log.info("삭제되었습니다: {}", chat);
        } else {
            log.info("아이디를 찾지 못했습니다: {}", messageId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 메시지 ID가 없습니다.");
        }
    }

}
