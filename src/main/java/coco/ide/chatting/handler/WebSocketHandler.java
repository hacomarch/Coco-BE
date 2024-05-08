package coco.ide.chatting.handler;


import coco.ide.chatting.requestDto.RequestChatDto;
import coco.ide.chatting.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class WebSocketHandler extends TextWebSocketHandler {
    private final ChatService chatService;

    // CLIENTS 변수에 세션을 담아두기 위한 공간
    private static final ConcurrentHashMap<String, WebSocketSession> CLIENTS = new ConcurrentHashMap<String, WebSocketSession>();

    // 웹소캣 서버에 접속하게 되면 동작하는 메소드
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);
        String memberIdString = session.getUri().getQuery().split("memberId=")[1];
        Long memberId = Long.parseLong(memberIdString);
        session.getAttributes().put("memberId = ", memberId);

        CLIENTS.put(session.getId(), session);
    }

    // 웹소켓 서버 접속이 끝났을 때 동작하는 메소드
    // CLIENTS 변수에 있는 세션도 제거
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        CLIENTS.remove(session.getId());
        System.out.println("session removed : " + session.getId());
    }

    // 사용자의 메시지를 받게되면 동작하는 메소드
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String id = session.getId();
        String memberIdString = session.getUri().getQuery().split("memberId=")[1];
        // memberId
        Long memberId = Long.parseLong(memberIdString);

        // 메시지 내용
        String payload = message.getPayload();

        RequestChatDto requestChatDto = RequestChatDto.builder()
                .memberId(memberId)
                .message(payload)
                .build();

        // 다른 사람들에게도 broadCasting 하기
        CLIENTS.entrySet().forEach(arg -> {
            if (!arg.getKey().equals(id)) {
                try {
                    arg.getValue().sendMessage(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        // DB 에 저장하기
        chatService.saveMessage(requestChatDto);
    }
}
