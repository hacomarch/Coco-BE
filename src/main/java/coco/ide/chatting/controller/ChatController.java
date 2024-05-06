package coco.ide.chatting.controller;

<<<<<<< HEAD
=======
import coco.ide.chatting.requestDto.RequestChatDto;
>>>>>>> e505255ca4bff2b21c6e8988a062225370c65514
import coco.ide.chatting.responseDto.ResponseChatDto;
import coco.ide.chatting.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
<<<<<<< HEAD
import org.springframework.web.bind.annotation.GetMapping;
=======
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
>>>>>>> e505255ca4bff2b21c6e8988a062225370c65514
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
