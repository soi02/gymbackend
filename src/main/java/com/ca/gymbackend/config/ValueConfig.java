package com.ca.gymbackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ValueConfig {
    @Bean(name = "fileRootPath")
    public String rootPath() {
        String os = System.getProperty("os.name").toLowerCase();
        String userHome = System.getProperty("user.home");
        String rootPath;

        if (os.contains("win")) {
            // Windows는 C드라이브 내 사용자 홈 폴더 기준
            rootPath = "C:/uploadFiles/";
        } else if (os.contains("mac")) {
            // macOS는 사용자 홈 폴더 기준
            rootPath = userHome + "/uploadFiles/";
        } else if (os.contains("nix") || os.contains("nux") || os.contains("aix")) {
            // Linux/Unix는 사용자 홈 폴더 기준
            rootPath = "/uploadFiles/";
        } else {
            return null;
        }
        return rootPath;
    }
}
