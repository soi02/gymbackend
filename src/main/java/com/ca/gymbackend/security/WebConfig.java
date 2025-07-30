package com.ca.gymbackend.security; // 현재 패키지명 유지

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    // 이미지가 저장되는 실제 파일 시스템 경로
    // 반드시 끝에 슬래시(/)를 붙여야 합니다.
    private final String externalUploadPath = "file:///C:/uploadFiles/"; 

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:5173")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
                .allowedHeaders("*")
                .allowCredentials(true);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // "/challengeImages/**" 패턴으로 들어오는 요청을
        // C:/uploadFiles/challengeImages/ 경로에서 찾도록 매핑합니다.
        registry.addResourceHandler("/challengeImages/**")
                .addResourceLocations(externalUploadPath + "challengeImages/"); // 외부 절대 경로 지정
        
        // ✅ Buddy 이미지 처리를 위한 "/uploadFiles/**" 요청을 "C:/uploadFiles/" 에서 찾도록 매핑
        // 이 경로는 user.profile_image가 "2025/07/30/..." 이런 형태일 때 작동합니다.
        registry.addResourceHandler("/uploadFiles/**") // <--- 이 줄 추가
                .addResourceLocations(externalUploadPath); // <--- 이 줄 추가 (C:/uploadFiles/ 전체를 매핑)
    }
}