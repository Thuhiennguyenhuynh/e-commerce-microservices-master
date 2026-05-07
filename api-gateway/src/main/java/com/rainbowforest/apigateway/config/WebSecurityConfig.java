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
	
// 	@Override
// 	protected void configure(HttpSecurity http) throws Exception {
// 		http.cors()
// 			.and()
// 			.csrf().disable()
// 			.authorizeRequests()
// 			.antMatchers("/", "/api/**").permitAll()
// 			.anyRequest().permitAll();
// 	}

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
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebFluxSecurity
public class WebSecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http
            .cors() // Kích hoạt CORS trong Spring Security
            .and()
            .csrf().disable() // Thường disable CSRF ở API Gateway
            .authorizeExchange()
            .anyExchange().permitAll(); // Cho phép tất cả đi qua Gateway
            
        return http.build();
    }

    // Cấu hình Global CORS cho WebFlux
    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration config = new CorsConfiguration();
        
        config.setAllowCredentials(true); // Cho phép gửi cookie, thông tin xác thực
        config.addAllowedOriginPattern("*"); // Cho phép mọi domain gọi tới
        config.addAllowedHeader("*"); // Cho phép mọi header
        config.addAllowedMethod("*"); // Cho phép mọi method (GET, POST, PUT, DELETE, OPTIONS)
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config); // Áp dụng cho mọi endpoint
        
        return new CorsWebFilter(source);
    }
}