package coco.ide.ideapp.files.run;

import lombok.RequiredArgsConstructor;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
public class JavaExecutionWebSocketHandler extends TextWebSocketHandler {
    private final CodeExecuteService codeExecuteService;

    private static ConcurrentHashMap<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    private ObjectMapper objectMapper = new ObjectMapper();
    private Process process;
    private BufferedWriter processInputWriter;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.put(session.getId(), session);
        session.sendMessage(new TextMessage("SessionId:" + session.getId()));
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        Map<String, String> jsonMessage = objectMapper.readValue(message.getPayload(), Map.class);
        String command = jsonMessage.get("command");

        if ("input".equals(command)) {
            String input = jsonMessage.get("data");
            codeExecuteService.handleUserInput(input);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        if (process != null && process.isAlive()) {
            process.destroy();
        }
        if (processInputWriter != null) {
            processInputWriter.close();
        }
        sessions.remove(session.getId());
    }

    public static WebSocketSession getSessionById(String sessionId) {
        return sessions.get(sessionId);
    }
}
