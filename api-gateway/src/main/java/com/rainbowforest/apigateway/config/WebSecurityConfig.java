    // package com.rainbowforest.apigateway.config;

    // import org.springframework.web.filter.CorsFilter;
    // import org.springframework.context.annotation.Bean;
    // import org.springframework.context.annotation.Configuration;
    // import org.springframework.security.config.annotation.web.builders.HttpSecurity;
    // import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
    // import org.springframework.web.cors.CorsConfiguration;
    // import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

    // @Configuration
    // public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
        
    //     @Override
    //     protected void configure(HttpSecurity http) throws Exception {
    //         http.csrf().disable().authorizeRequests().antMatchers("/").permitAll();
    //     }
    // // Cấu hình Global CORS
    //     @Bean
    //     public CorsFilter corsFilter() {
    //         UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    //         CorsConfiguration config = new CorsConfiguration();
            
    //         config.setAllowCredentials(true); // Cho phép gửi cookie, thông tin xác thực
    //         config.addAllowedOriginPattern("*"); // Cho phép mọi domain (kể cả localhost:3000) gọi tới
    //         config.addAllowedHeader("*"); // Cho phép mọi header
    //         config.addAllowedMethod("*"); // Cho phép mọi method (GET, POST, PUT, DELETE, OPTIONS)
            
    //         source.registerCorsConfiguration("/**", config); // Áp dụng cho mọi endpoint
    //         return new CorsFilter(source);
    //     }
    // }


    package com.rainbowforest.apigateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

@Configuration
public class WebSecurityConfig {

    // Cấu hình phân quyền cho WebFlux
    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http.csrf().disable()
            .authorizeExchange()
            .anyExchange().permitAll();
        return http.build();
    }

    // Cấu hình Global CORS cho WebFlux (Sử dụng CorsWebFilter thay vì CorsFilter)
    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration config = new CorsConfiguration();
        
        config.setAllowCredentials(true); // Cho phép gửi cookie, thông tin xác thực
        config.addAllowedOriginPattern("*"); // Cho phép mọi domain (kể cả localhost:3000) gọi tới
        config.addAllowedHeader("*"); // Cho phép mọi header
        config.addAllowedMethod("*"); // Cho phép mọi method (GET, POST, PUT, DELETE, OPTIONS)
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config); // Áp dụng cho mọi endpoint
        
        return new CorsWebFilter(source);
    }
}