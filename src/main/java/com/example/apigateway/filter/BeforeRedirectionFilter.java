package com.example.apigateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.OrderedGatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;

@Component
public class BeforeRedirectionFilter extends AbstractGatewayFilterFactory<BeforeRedirectionFilter.Config> {
    private final Logger logger = LoggerFactory.getLogger(RedirectionFilter.class);

    public BeforeRedirectionFilter() {
        super(Config.class);
    }
    @Override
    public GatewayFilter apply(Config config) {
        return new OrderedGatewayFilter((exchange, chain) -> {
            logger.info("BEFORE REDIRECTION-------------------");
            return chain.filter(exchange);
        }, 0);
    }

    public static class Config {
        private String name;

        public Config(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
