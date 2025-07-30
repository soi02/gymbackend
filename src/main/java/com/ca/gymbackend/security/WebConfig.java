package com.ca.gymbackend.security; // 현재 패키지명 유지

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry; // 이 import 추가
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
        
        // 만약 src/main/resources/static 또는 src/main/resources/public 에
        // 다른 정적 파일(CSS, JS, 기본 이미지 등)이 있다면,
        // Spring Boot가 기본적으로 이들을 서빙하지만, 명시적으로 추가하는 것도 좋은 방법입니다.
        // registry.addResourceHandler("/**")
        //         .addResourceLocations("classpath:/static/", "classpath:/public/");
    }
}