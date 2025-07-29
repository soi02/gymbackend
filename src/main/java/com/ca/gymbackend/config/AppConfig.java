package com.ca.gymbackend.config;

import java.net.http.HttpClient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
// import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;

@Configuration
public class AppConfig {
    @Autowired
    @Qualifier("fileRootPath")
    private String rootPath;

    // @Override
    // public void addResourceHandlers(ResourceHandlerRegistry registry) {
    //     // System.out.println("gggg:" + rootPath);
    //     registry.addResourceHandler("/uploadFiles/**")
    //             .addResourceLocations("file:///" + rootPath);
    // }

    @Bean
    public HttpClient httpClient() {
        return HttpClient.newHttpClient();
    }

    @Bean
    public RestClient restClient() {
        return RestClient.builder().build();
    }
}
