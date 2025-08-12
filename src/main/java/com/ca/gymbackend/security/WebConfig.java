package com.ca.gymbackend.security; // 이 패키지 또는 com.ca.gymbackend.config 패키지로 이동

import org.springframework.beans.factory.annotation.Autowired; // 추가
import org.springframework.beans.factory.annotation.Qualifier; // 추가
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    // ValueConfig에서 정의한 "fileRootPath" 빈을 주입받습니다.
    @Autowired
    @Qualifier("fileRootPath")
    private String fileRootPath; // 변수명도 명확하게 변경 (externalUploadPath 대신)

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        System.out.println("DEBUG: CORS 설정이 적용되었습니다.");
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
                .allowedHeaders("*")
                .allowCredentials(true);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        // 디버그 출력도 주입받은 경로를 사용하도록 변경
        System.out.println("DEBUG: fileRootPath = " + fileRootPath);
        System.out.println("DEBUG: Challenge Images Path = " + fileRootPath + "challengeImages/");
        System.out.println("DEBUG: Upload Files Path = " + fileRootPath);

        registry.addResourceHandler("/challengeImages/**")
                .addResourceLocations("file:///" + fileRootPath + "challengeImages/"); // file:/// 접두사 명시

        // 출석 사진
        registry.addResourceHandler("/attendancePhotos/**")
                .addResourceLocations("file:///" + fileRootPath + "attendancePhotos/");
        
        registry.addResourceHandler("/uploadFiles/**")
                .addResourceLocations("file:///" + fileRootPath); // file:/// 접두사 명시
    }
}