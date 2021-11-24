package com.example.apigateway.filter;

import com.example.apigateway.util.ApplicationProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.OrderedGatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR;

@Component
public class RedirectionFilter extends AbstractGatewayFilterFactory<RedirectionFilter.RedirectionFilterConfig> {

    private final Logger logger = LoggerFactory.getLogger(RedirectionFilter.class);

    private static final String X_ORG_ID = "X-Org-Id";
    private static final String X_CLIENT_PORTAL = "X-Portal";
    private static final String ORGANISATION_MAP = "organisations";

    private final ApplicationProperties applicationProperties;

    @Autowired
    public RedirectionFilter(ApplicationProperties applicationProperties) {
        super(RedirectionFilterConfig.class);
        this.applicationProperties = applicationProperties;
    }

    @Override
    public GatewayFilter apply(RedirectionFilterConfig config) {
        return new OrderedGatewayFilter((exchange, chain) -> {

            logger.info("Inside redirection filter");

            ServerHttpRequest serverHttpRequest = exchange.getRequest();
            Map<String, String> headers = serverHttpRequest.getHeaders().toSingleValueMap();

            logger.info("REQUEST UUID: " + headers.get("uuid"));

            String path = serverHttpRequest.getPath().toString();
            String clientPortalIdentifier = headers.get(X_CLIENT_PORTAL);

            RouteDefinition requestRoute = getRoute(clientPortalIdentifier);

            if (requestRoute == null) {
                return chain.filter(exchange);
            }

            String organisationId = headers.get(X_ORG_ID);

            if (!canRedirect(organisationId, requestRoute)) {
                return chain.filter(exchange);
            }

            URI redirectURI = null;
            try {
                redirectURI = buildURI(requestRoute);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }

            ServerHttpRequest modifiedRequest = exchange
                    .getRequest()
                    .mutate()
                    .uri(redirectURI)
                    .build();

            ServerWebExchange modifiedExchange = exchange
                    .mutate()
                    .request(modifiedRequest)
                    .build();

            modifiedExchange.getAttributes().put(GATEWAY_REQUEST_URL_ATTR, redirectURI);

            return chain.filter(modifiedExchange);

        }, 1);
    }

    private URI buildURI(RouteDefinition route) throws URISyntaxException {
        Map<String, Object> metadata = route.getMetadata();
        String redirectURI = metadata.get("redirectURI").toString();

        return new URI(redirectURI + "/portal/redirected");
    }

    private boolean canRedirect(String organisationId, RouteDefinition routeDefinition) {
        Map<String, Object> metadata = routeDefinition.getMetadata();
        Map<String, String> organisations = (Map<String, String>) metadata.get(ORGANISATION_MAP);

        return organisations.containsValue(organisationId);
    }

    private RouteDefinition getRoute(String clientPortalIdentifier) {
        List<RouteDefinition> routeDefinitions = applicationProperties.getRoutes();
        RouteDefinition requestRoute = null;

        for (RouteDefinition routeDefinition : routeDefinitions) {
            if (routeDefinition.getId().equals(clientPortalIdentifier)) {
                requestRoute = routeDefinition;
            }
        }

        return requestRoute;
    }

    public static class RedirectionFilterConfig {

    }
}
