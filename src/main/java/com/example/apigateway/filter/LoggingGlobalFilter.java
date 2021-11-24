package com.example.apigateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
public class LoggingGlobalFilter implements GlobalFilter {

    final Logger logger = LoggerFactory.getLogger(LoggingGlobalFilter.class);

    private static final String X_ORG_ID = "X-Org-Id";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        Map<String, String> headers = request.getHeaders().toSingleValueMap();

        logger.info("REQUEST ID: " + request.getId());
        logger.info("METHOD: " + request.getMethod().name());
        logger.info("PATH: " + request.getPath());
        logger.info("X-ORG-CODE: " + headers.get(X_ORG_ID));

        return chain.filter(exchange);
    }
}
