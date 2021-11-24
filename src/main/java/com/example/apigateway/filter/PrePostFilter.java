package com.example.apigateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
public class PrePostFilter implements WebFilter {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final String X_REQUEST_UUID = "X-REQUEST-UUID";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String UUID = java.util.UUID.randomUUID().toString();

        logger.info("INSIDE INBOUND FILTER");

        ServerHttpRequest request = exchange.getRequest().mutate()
                .header(X_REQUEST_UUID, UUID)
                .build();

        logger.info("X_PORTAL_ID Header included");

        return chain.filter(exchange.mutate().request(request).build())
                .transformDeferred((call) -> call.doFinally(signalType -> {
                    ServerHttpResponse response = exchange.getResponse();

                    logger.info("Response status: " + response.getStatusCode().name());
                    logger.info("AFTER REQUEST POST FILTER");
                }));
    }
}
