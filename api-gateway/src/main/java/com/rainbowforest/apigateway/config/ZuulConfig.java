package com.rainbowforest.apigateway.config;

import org.springframework.cloud.netflix.zuul.filters.RouteLocator;
import org.springframework.cloud.netflix.zuul.web.ZuulController;
import org.springframework.cloud.netflix.zuul.web.ZuulHandlerMapping;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ZuulConfig {

    @Bean
    public ZuulHandlerMapping zuulHandlerMapping(RouteLocator routes, ZuulController zuulController) {
        ZuulHandlerMapping mapping = new ZuulHandlerMapping(routes, zuulController);
        
        // MẸO QUAN TRỌNG: Gán ErrorController thành null 
        // để Zuul không bao giờ gọi hàm getErrorPath() bị lỗi của Spring Boot 2.7 nữa.
        mapping.setErrorController(null);
        
        return mapping;
    }
}