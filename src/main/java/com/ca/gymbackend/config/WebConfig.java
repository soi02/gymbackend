package com.ca.gymbackend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

@Configuration
@EnableWebSocketMessageBroker
public class WebConfig {
public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws") // 연결 경로
                .setAllowedOrigins("*")
                .withSockJS(); // SockJS 사용
    }

    
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic"); // 구독 주소
        registry.setApplicationDestinationPrefixes("/app"); // 보낼 때
    }
}
