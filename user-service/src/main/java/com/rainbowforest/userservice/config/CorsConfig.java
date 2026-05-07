package com.rainbowforest.userservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Áp dụng cho tất cả các API
                .allowedOriginPatterns("*") // Cho phép tất cả các domain (Frontend) gọi tới
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH") // Cho phép các phương thức HTTP
                .allowedHeaders("*") // Cho phép tất cả các header
                .allowCredentials(true) // Cho phép gửi cookie/token
                .maxAge(3600); // Lưu cache cấu hình CORS trong 1 giờ để giảm tải request OPTIONS
    }
}