package coco.ide.chatting.controller;

import coco.ide.chatting.requestDto.RequestChatDto;
import coco.ide.chatting.responseDto.ResponseChatDto;
import coco.ide.chatting.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;

    private final SimpMessagingTemplate simpMessagingTemplate;

    // 채팅 메시지 수신 및 저장
    @MessageMapping("/message")
    public ResponseEntity<String> saveMessage(@RequestBody RequestChatDto requestChatDto) {
        // 메시지 저장
        ResponseChatDto responseChatDto = chatService.saveMessage(requestChatDto);

        // 메시지를 모든 유저들에게 전송
        simpMessagingTemplate.convertAndSend("/topic/chat", responseChatDto);

        //
        return ResponseEntity.ok("전체 유저에게 메시지 전송 완료!");
    }

    // 모든 메시지 기록 가져오기
    @GetMapping("/messages")
    public List<ResponseChatDto> allMessages() {
        return chatService.allMessage();
    }

    // 메시지 삭제 "isDeleted=true" 로 변경
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/message")
    public void deleteMessage(@RequestParam(name = "messageId") Long messageId) {
        chatService.deleteMessage(messageId);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/message")
    public List<ResponseChatDto> searchMessages(@RequestParam("search") String word) {
        List<ResponseChatDto> wordList = chatService.searchMessage(word);
        return wordList;
    }
}
