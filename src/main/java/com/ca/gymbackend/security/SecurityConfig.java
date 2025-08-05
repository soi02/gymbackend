package com.ca.gymbackend.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration; // CorsConfiguration import 추가
import org.springframework.web.cors.CorsConfigurationSource; // CorsConfigurationSource import 추가
import org.springframework.web.cors.UrlBasedCorsConfigurationSource; // UrlBasedCorsConfigurationSource import 추가
import jakarta.servlet.http.HttpServletResponse;

import java.util.Arrays;
import java.util.List;

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
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // 이 부분의 주석을 해제하세요.
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth

                        // ✅ 이 부분을 클라이언트의 URL과 동일하게 수정하세요.
                        .requestMatchers("/ws/chat/**").permitAll()
                        .requestMatchers("/ws/group-chat/**").permitAll()// ✅ 그룹 채팅 웹소켓 경로 추가
                        .requestMatchers(HttpMethod.POST, "/api/user/login").permitAll()
                        .requestMatchers("/api/user/verify-token").permitAll() // 토큰 유효성 검증은 permilAll
                        .requestMatchers("/api/challenge/tendency-test/status").authenticated() // 성향 테스트 상태 조회는 인증 필요
                        .requestMatchers(HttpMethod.GET, "/api/routine/getRoutinesByUserId/**").authenticated() // 루틴
                                                                                                                // 조회는
                                                                                                                // 인증 필요
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

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // setAllowedOriginPatterns 대신 setAllowedOrigins를 사용해도 무방합니다.
        configuration.setAllowedOrigins(List.of("http://localhost:5173"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD")); // HEAD 메서드를
                                                                                                           // 추가하세요.
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Auth-Token")); // 클라이언트에서 보낼
                                                                                                         // 헤더를 명시하는 것이
                                                                                                         // 좋습니다.
        configuration.setAllowedHeaders(List.of("*")); // ✅ 이 코드를 추가해 보세요.                                                                                                 
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}