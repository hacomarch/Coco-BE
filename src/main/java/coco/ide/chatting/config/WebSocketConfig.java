package coco.ide.chatting.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry){
        registry.addEndpoint("/ws")         // stomp 통신
                .setAllowedOrigins("http://localhost:8080", "http://localhost:3000")
                .withSockJS();
    }
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry){
        registry.enableSimpleBroker("/topic");    // 구독하는 사용자들에게 메시지 전달
        registry.setApplicationDestinationPrefixes("/app");        // 메세지 송신(SEND 요청 처리)
    }
}
