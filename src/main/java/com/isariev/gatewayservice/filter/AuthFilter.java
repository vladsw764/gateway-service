package com.isariev.gatewayservice.filter;

import com.isariev.gatewayservice.config.RedisHashProcessor;
import com.isariev.gatewayservice.dto.ApiKey;
import com.isariev.gatewayservice.util.MapperUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@Slf4j
public class AuthFilter implements GlobalFilter, Ordered {

    private final RedisHashProcessor redisHashProcessor;

    @Value("${redis.record.key}")
    private String RECORD_KEY;


    public AuthFilter(RedisHashProcessor redisHashProcessor) {
        this.redisHashProcessor = redisHashProcessor;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        List<String> apiKeyHeader = exchange.getRequest().getHeaders().get("X-Yoda-Api-Key");

        log.info("api key: {}", apiKeyHeader);

        Route route = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);
        String routeId = route != null ? route.getId() : null;

        if (routeId == null || CollectionUtils.isEmpty(apiKeyHeader) || !isAuthorize(routeId, apiKeyHeader.get(0))) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Access denied. Please ensure that your API KEY is valid and authorized to consume this service.");
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

    private boolean isAuthorize(String routeId, String apiKey) {
        Object apiKeyObject = redisHashProcessor.getHashValue(RECORD_KEY, apiKey);
        if (apiKeyObject != null) {
            ApiKey key = MapperUtils.objectMapper(apiKeyObject, ApiKey.class);
            return key.getServices().contains(routeId);
        }
        return false;
    }
}
