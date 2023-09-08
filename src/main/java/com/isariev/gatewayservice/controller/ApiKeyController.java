package com.isariev.gatewayservice.controller;

import com.isariev.gatewayservice.config.RedisHashProcessor;
import com.isariev.gatewayservice.dto.ApiKey;
import com.isariev.gatewayservice.util.MapperUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@RestController
@Slf4j
public class ApiKeyController {

    private final RedisHashProcessor redisHashProcessor;

    @Value("${redis.record.key}")
    private static String RECORD_KEY;

    public ApiKeyController(RedisHashProcessor redisHashProcessor) {
        this.redisHashProcessor = redisHashProcessor;
    }

    /**
     * Generates a new API key for a specific service.
     *
     * @param serviceName The name of the service for which the API key is generated.
     * @return The generated API key.
     */
    @PostMapping("/generate")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiKey generateApiKeyForPaymentService(@RequestParam(name = "service-name") String serviceName) {
        ApiKey apiKey = new ApiKey(String.valueOf(UUID.randomUUID()), List.of(serviceName));
        redisHashProcessor.setHashValue(RECORD_KEY, apiKey.getKey(), apiKey);

        log.info("API Key saved: {}", apiKey.getKey());
        return apiKey;
    }

    /**
     * Retrieves an API key by its key.
     *
     * @param apiKey The key of the API key to retrieve.
     * @return The API key if found.
     * @throws ResponseStatusException if the API key is not found.
     */
    @GetMapping("/{apiKey}")
    public ApiKey getApiKey(@PathVariable String apiKey) {
        Object apiKeyObject = redisHashProcessor.getHashValue(RECORD_KEY, apiKey);

        if (apiKeyObject != null) {
            log.info("API Key found: {}", apiKey);
            return MapperUtils.objectMapper(apiKeyObject, ApiKey.class);
        }
        log.warn("API Key not found: {}", apiKey);
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "API Key not found");
    }
}
