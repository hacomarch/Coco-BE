package coco.ide.chatting.config;


import coco.ide.ideapp.files.run.CodeExecuteService;
import coco.ide.ideapp.files.run.JavaExecutionWebSocketHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {

    private final WebSocketHandler webSocketHandler;
    private final CodeExecuteService codeExecuteService;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(webSocketHandler, "/topic")
                .setAllowedOrigins("*");
        registry.addHandler(new JavaExecutionWebSocketHandler(codeExecuteService), "/execute-java")
                .setAllowedOrigins("*");
    }

}
