package com.ca.gymbackend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.io.IOException;
import java.util.Collections;

// 이 부분 다시 작성해야함

@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        // 웹소켓 핸드셰이크 요청은 필터링하지 않습니다.
        // URL이 /ws/chat으로 시작하면 필터를 통과시킵니다.
        String path = request.getRequestURI();
        boolean isWebSocketRequest = path.startsWith("/ws/chat") || path.startsWith("/ws/group-chat"); // 그룹 채팅 엔드포인트 추가
        System.out.println("[DEBUG] shouldNotFilter URL: " + path + ", Result: " + isWebSocketRequest);
        return isWebSocketRequest;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        System.out.println("[DEBUG] doFilterInternal 호출");
        String authHeader = request.getHeader("Authorization");
        System.out.println("[DEBUG] 요청 URL: " + request.getRequestURI());
        System.out.println("[DEBUG] Authorization Header: " + authHeader);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            if (token != null && jwtUtil.validateToken(token)) {
                System.out.println("[DEBUG] 토큰 유효성 검사 성공.");
                Integer userId = jwtUtil.getUserId(token);
                Authentication auth = new UsernamePasswordAuthenticationToken(
                        userId, null, Collections.emptyList());
                SecurityContextHolder.getContext().setAuthentication(auth);
            } else {
                System.out.println("[DEBUG] 토큰 유효성 검사 실패 또는 토큰 없음.");
            }
        } else {
            System.out.println("[DEBUG] Authorization 헤더가 누락되었거나 'Bearer'로 시작하지 않음.");
        }

        filterChain.doFilter(request, response);
    }

}