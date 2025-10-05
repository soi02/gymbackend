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

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> {}) // ★ WebMvc가 아니라 CorsConfig 빈 사용
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**").permitAll() // ★ Preflight 허용
                .requestMatchers("/ws/chat/**", "/ws/group-chat/**").permitAll()
                .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/user/login").permitAll()
                .requestMatchers("/api/user/verify-token").permitAll()
                .requestMatchers("/api/challenge/groupchat/listWithSummary/**").permitAll()
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/routine/getRoutinesByUserId/**").authenticated()
                .requestMatchers("/api/diary/emojis").permitAll()
                .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/diary/write").authenticated()
                .requestMatchers("/api/diary/check-today", "/api/diary/list", "/api/diary/date").authenticated()
                .requestMatchers("/api/buddy/is-buddy").permitAll()
                .requestMatchers("/api/buddy/**").authenticated()
                .anyRequest().permitAll()
            )
            .formLogin(f -> f.disable())
            .httpBasic(b -> b.disable())
            .addFilterBefore(jwtAuthFilter, org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class)
            .exceptionHandling(ex -> ex.authenticationEntryPoint(
                (req, res, e) -> res.sendError(jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized")
            ));
        return http.build();
    }
}