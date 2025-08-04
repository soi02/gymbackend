package com.ca.gymbackend.config;

import com.ca.gymbackend.security.JwtUtil;
// import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
// import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
// import org.springframework.web.socket.server.standard.ServerEndpointExporter; // 이 클래스를 import해야 합니다.
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketConfig.class);

    private final JwtUtil jwtUtil;

    public WebSocketConfig(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic");
        registry.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws/chat")
                .setAllowedOriginPatterns("http://localhost:5173")
                .withSockJS();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

                logger.info("STOMP 명령: {}", accessor.getCommand());

                // CONNECT 명령일 때만 토큰 검증
                // if (StompCommand.CONNECT.equals(accessor.getCommand())) {

                //     String jwtToken = accessor.getFirstNativeHeader("Authorization");
                //     logger.info("STOMP Authorization 헤더: {}", jwtToken);

                //     if (jwtToken == null || !jwtToken.startsWith("Bearer ")) {
                //         logger.error("JWT 토큰이 누락되었거나 형식이 잘못되었습니다. 연결을 거부합니다.");
                //         // NullPointerException 방지를 위해 명시적으로 null을 반환하여 연결을 끊습니다.
                //         return null;
                //     }

                //     String token = jwtToken.substring(7);
                //     logger.info("추출된 JWT 토큰: {}", token);

                //     try {
                //         boolean isValid = jwtUtil.validateToken(token); // 토큰 검증 결과를 변수에 저장
                //         logger.info("jwtUtil.validateToken({}) 결과: {}", token, isValid); // 검증 결과를 로그로 출력

                //         if (!isValid) {
                //             logger.error("토큰이 유효하지 않습니다. 연결을 거부합니다.");
                //             return null;
                //         }
                //         logger.info("JWT 토큰이 성공적으로 검증되었습니다. 연결을 허용합니다.");
                //     } catch (Exception e) {
                //         logger.error("토큰 검증 중 예외 발생: {}", e.getMessage());
                //         return null;
                //     }
                // }
                return message;
            }
        });
    }
}