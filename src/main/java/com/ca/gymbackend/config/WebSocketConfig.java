package com.ca.gymbackend.config;

import com.ca.gymbackend.security.JwtUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketConfig.class);

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // ✅ 여기만 고치면 핵심 문제 해결
        registry.enableSimpleBroker("/topic", "/topic-group");
        registry.setApplicationDestinationPrefixes("/app");
        // registry.setUserDestinationPrefix("/user"); // 개인큐 필요시
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // ✅ 순수 WebSocket 사용중 (클라에서 brokerURL=ws://... 로 접속해야 함)
        registry.addEndpoint("/ws/chat")
                .setAllowedOriginPatterns("http://localhost:5173");
                // .setAllowedOriginPatterns("http://localhost:5173","https://gymmadang.null-pointer-exception.com");

        registry.addEndpoint("/ws/group-chat")
                .setAllowedOriginPatterns("http://localhost:5173");
                // .setAllowedOriginPatterns("http://localhost:5173","https://gymmadang.null-pointer-exception.com");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
                if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
                    String authorizationHeader = accessor.getFirstNativeHeader("Authorization");
                    logger.info("STOMP CONNECT 헤더: {}", authorizationHeader);

                    if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                        String token = authorizationHeader.substring(7);
                        try {
                            Integer userId = jwtUtil.getUserId(token);
                            accessor.setUser(() -> String.valueOf(userId));
                            logger.info("✅ STOMP 연결 인증 성공. 사용자 ID: {}", userId);
                        } catch (Exception e) {
                            logger.error("❌ STOMP 연결 토큰 인증 실패: {}", e.getMessage());
                            throw new IllegalArgumentException("토큰이 유효하지 않습니다.");
                        }
                    } else {
                        logger.error("❌ STOMP CONNECT 헤더에 토큰이 없습니다.");
                        throw new IllegalArgumentException("토큰이 필요합니다.");
                    }
                }
                return message;
            }
        });
    }
}
