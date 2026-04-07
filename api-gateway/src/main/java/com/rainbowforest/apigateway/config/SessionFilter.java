package com.rainbowforest.apigateway.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class SessionFilter implements GlobalFilter, Ordered {

    private static final Logger logger = LoggerFactory.getLogger(SessionFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return exchange.getSession().flatMap(session -> {
            String sessionId = session.getId();
            logger.info("Session ID đi qua Gateway: " + sessionId);
            
            // Gắn Session ID vào header giả lập Cookie để đẩy xuống Microservices
            ServerWebExchange mutatedExchange = exchange.mutate().request(
                    exchange.getRequest().mutate()
                            .header("Cookie", sessionId)
                            .build()
            ).build();

            return chain.filter(mutatedExchange);
        });
    }

    @Override
    public int getOrder() {
        return 10;
    }
}