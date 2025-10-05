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
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        String method = request.getMethod();

        // 1) Preflight(OPTIONS)는 건드리지 않음
        if ("OPTIONS".equalsIgnoreCase(method)) return true;

        // 2) 로그인/토큰검증은 JWT 없이 접근
        if ("/api/user/login".equals(path) && "POST".equalsIgnoreCase(method)) return true;
        if ("/api/user/verify-token".equals(path) && "POST".equalsIgnoreCase(method)) return true;

        // 3) WebSocket 핸드셰이크 스킵
        if (path.startsWith("/ws/chat") || path.startsWith("/ws/group-chat")) return true;

        return false;
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