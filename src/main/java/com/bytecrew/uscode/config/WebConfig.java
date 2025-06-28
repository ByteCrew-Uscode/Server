package com.bytecrew.uscode.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("exp://192.168.0.66:8081", "http://192.168.0.66:8081", "http://localhost:5173", "https://usfarmtools.vercel.app") // 프론트 주소
                .allowedMethods("*")
                .allowedHeaders("*")
                .allowCredentials(true); // 필요한 경우
    }
}