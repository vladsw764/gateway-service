package com.isariev.gatewayservice.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RouteConfig {

    @Bean
    public RouteLocator customRoutLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("payment-service", r -> r.path("/payment-service/**")
                        .filters(f -> f.stripPrefix(1)).uri("http://payment-service:8081")).build();
    }
}
