package com.ca.gymbackend.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    // @Bean
    // public SecurityFilterChain securityFilterChain(HttpSecurity http) throws
    // Exception {
    // http
    // .csrf(csrf -> csrf.disable())
    // .sessionManagement(session ->
    // session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
    // .authorizeHttpRequests(auth -> auth
    // .requestMatchers("/ws-buddy/**").permitAll() // ✅ WebSocket 경로 허용
    // .requestMatchers(HttpMethod.POST, "/api/article").authenticated()
    // .requestMatchers(HttpMethod.PUT, "/api/article/*").authenticated()
    // .requestMatchers(HttpMethod.DELETE, "/api/article/*").authenticated()
    // .anyRequest().permitAll())
    // .formLogin(form -> form.disable())
    // .httpBasic(basic -> basic.disable())
    // .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
    // .exceptionHandling(ex -> ex
    // .authenticationEntryPoint((request, response, authException) -> {
    // response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
    // }));

    // return http.build();
    // }

        @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        System.out.println("DEBUG: SecurityFilterChain 설정이 적용되었습니다.");

        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configure(http)) // WebConfig의 CORS 설정을 사용
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth

                        // ✅ 이 부분을 클라이언트의 URL과 동일하게 수정하세요.
                        .requestMatchers("/ws/chat/**").permitAll()
                        .requestMatchers("/ws/group-chat/**").permitAll()// ✅ 그룹 채팅 웹소켓 경로 추가
                        .requestMatchers(HttpMethod.POST, "/api/user/login").permitAll()
                        .requestMatchers("/api/user/verify-token").permitAll() // 토큰 유효성 검증은 permilAll
                        .requestMatchers("/api/challenge/tendency-test/status").authenticated() // 성향 테스트 상태 조회는 인증 필요
                        .requestMatchers(HttpMethod.GET, "/api/routine/getRoutinesByUserId/**").authenticated() // 루틴 조회는 인증 필요
                        .requestMatchers("/api/diary/emojis").permitAll() // 이모지 목록은 인증 없이 조회 가능
                        .requestMatchers(HttpMethod.POST, "/api/diary/write").authenticated() // 일기 작성은 인증 필요
                        .requestMatchers("/api/diary/check-today").authenticated() // 일기 작성 여부 확인은 인증 필요
                        .requestMatchers("/api/diary/list").authenticated() // 일기 목록 조회는 인증 필요
                        .requestMatchers("/api/diary/date").authenticated() // 특정 날짜 일기 조회는 인증 필요

                        .requestMatchers("/api/challenge/groupchat/listWithSummary/**").permitAll()
                        .anyRequest().permitAll()) // 그 외 모든 요청은 허용

                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
                        }));



        return http.build();
    }

    // CORS 설정은 WebConfig에서 관리합니다.
}