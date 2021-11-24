package com.example.apigateway.configuration;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//@Configuration
public class GatewayConfiguration {

    //@Bean
    public RouteLocator routeLocator(RouteLocatorBuilder routeLocatorBuilder) {
        return routeLocatorBuilder.routes()
                .route("client-portal", r -> r.path("/portal")
                        .and().method("POST",  "PUT", "DELETE")
                        .and().host("localhost*")
                        .uri("http://localhost:8081"))
                .build();
    }
}
