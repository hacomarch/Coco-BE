package coco.ide.chatting.controller;

import coco.ide.chatting.responseDto.ResponseChatDto;
import coco.ide.chatting.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;

    // 메시지 전체 조회하기
    @GetMapping("/messages")
    public List<ResponseChatDto> allMessages() {
        return chatService.allMessages();
    }
}