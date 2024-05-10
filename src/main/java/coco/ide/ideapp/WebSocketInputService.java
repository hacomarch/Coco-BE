//package coco.ide.ideapp;
//
//import jakarta.websocket.OnClose;
//import jakarta.websocket.OnMessage;
//import jakarta.websocket.OnOpen;
//import jakarta.websocket.Session;
//import org.springframework.stereotype.Service;
//
//import java.io.OutputStream;
//import java.io.PipedOutputStream;
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//
//@Service
//public class WebSocketInputService {
//
//    // Session과 연결된 OutputStream 저장
//    private Map<String, OutputStream> userOutputStreams = new ConcurrentHashMap<>();
//
//    // 웹소켓 세션 열기
//    @OnOpen
//    public void onOpen(Session session) {
//        userOutputStreams.put(session.getId(), new PipedOutputStream());
//        // 프로세스 실행 로직에 OutputStream 연결
//        startProcessWithInput(session.getId(), userOutputStreams.get(session.getId()));
//    }
//
//    // 웹소켓을 통한 메시지 수신
//    @OnMessage
//    public void onMessage(String message, Session session) {
//        OutputStream outputStream = userOutputStreams.get(session.getId());
//        if (outputStream != null) {
//            outputStream.write(message.getBytes());
//            outputStream.flush();
//        }
//    }
//
//    // 웹소켓 세션 닫기
//    @OnClose
//    public void onClose(Session session) {
//        OutputStream outputStream = userOutputStreams.remove(session.getId());
//        if (outputStream != null) {
//            outputStream.close();
//        }
//    }
//
//    private void startProcessWithInput(String sessionId, OutputStream outputStream) {
//        ProcessBuilder processBuilder = new ProcessBuilder("java", "Main");
//        processBuilder.redirectInput(ProcessBuilder.Redirect.from(outputStream));
//        // 프로세스 시작 로직 추가
//    }
//}
