package com.ca.gymbackend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    // @Override
    // public void registerStompEndpoints(StompEndpointRegistry registry) {
    //     registry.addEndpoint("/ws")
    //             .setAllowedOrigins("http://localhost:5173")  // 명시적 origin
    //             .withSockJS();
    // }

    // @Override
    // public void configureMessageBroker(MessageBrokerRegistry registry) {
    //     registry.enableSimpleBroker("/topic");
    //     registry.setApplicationDestinationPrefixes("/app");
    // }
    //  @Override
    // public void registerStompEndpoints(StompEndpointRegistry registry) {
    //     registry.addEndpoint("/ws") // <== 클라이언트는 이걸로 연결해
    //             .setAllowedOriginPatterns("*") // 프론트 주소에 따라 조정 가능
    //             .withSockJS(); // SockJS 사용
    // }

    // @Override
    // public void configureMessageBroker(MessageBrokerRegistry registry) {
    //     registry.enableSimpleBroker("/topic"); // 구독 경로
    //     registry.setApplicationDestinationPrefixes("/app"); // 전송 경로
    // }
    //  @Override
    // public void configureMessageBroker(MessageBrokerRegistry registry) {
    //     // 클라이언트에게 메시지를 보낼 때 사용할 prefix를 설정합니다.
    //     // "/topic"은 1:N 브로드캐스팅, "/queue"는 1:1 메시징에 주로 사용됩니다.
    //     registry.enableSimpleBroker("/topic");
        
    //     // 클라이언트가 서버로 메시지를 보낼 때 사용할 prefix를 설정합니다.
    //     // 예를 들어, 클라이언트는 "/app/chat"으로 메시지를 보낼 수 있습니다.
    //     registry.setApplicationDestinationPrefixes("/app");
    // }

    // @Override
    // public void registerStompEndpoints(StompEndpointRegistry registry) {
    //     // WebSocket 연결을 위한 STOMP 엔드포인트를 설정합니다.
    //     // "/ws-chat"으로 접속하면 WebSocket 연결이 가능해집니다.
    //     // `withSockJS()`는 WebSocket을 지원하지 않는 브라우저에서도
    //     // SockJS를 통해 연결할 수 있게 해줍니다.
    //     registry.addEndpoint("/ws-chat").setAllowedOriginPatterns("*").withSockJS();
    // }


    // @Override
    // public void configureMessageBroker(MessageBrokerRegistry config) {
    //     config.enableSimpleBroker("/topic"); // 브로드캐스트 채널
    //     config.setApplicationDestinationPrefixes("/app"); // 메시지 보낼 때 프론트가 사용하는 prefix
    // }

    // @Override
    // public void registerStompEndpoints(StompEndpointRegistry registry) {
    //     registry.addEndpoint("/ws-buddy")
    //             .setAllowedOriginPatterns("*")
    //             .withSockJS(); // SockJS 사용
    // }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-buddy") // 이 주소 프론트에서 연결
                .setAllowedOriginPatterns("*")
                .withSockJS(); // ← 이거 중요!
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic", "/queue");
        registry.setApplicationDestinationPrefixes("/app");
    }
}