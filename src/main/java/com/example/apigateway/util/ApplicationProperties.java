package com.example.apigateway.util;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "spring.cloud.gateway")
@PropertySource(value = {"classpath:application.yml"}, factory = YamlPropertySourceFactory.class)
public class ApplicationProperties {
    private List<String> organisations;
    private List<RouteDefinition> routes;

    public List<String> getOrganisations() {
        return organisations;
    }

    public void setOrganisations(List<String> organisations) {
        this.organisations = organisations;
    }

    public void setRoutes(List<RouteDefinition> routes) {
        this.routes = routes;
    }

    public List<RouteDefinition> getRoutes() {
        return routes;
    }
}
