package com.isariev.gatewayservice.controller;

import com.isariev.gatewayservice.config.RedisHashProcessor;
import com.isariev.gatewayservice.dto.ApiKey;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;
import java.util.UUID;

@SpringBootTest
@AutoConfigureWebTestClient
@ActiveProfiles("test")
public class ApiKeyControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private RedisHashProcessor redisHashProcessor;

    @Value("${redis.record.key}")
    private String RECORD_KEY;

    @Test
    void testGenerateApiKey() {
        String serviceName = "test-service";
        webTestClient.post()
                .uri("/generate?service-name={serviceName}", serviceName)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(ApiKey.class)
                .value(apiKey -> {
                    assert apiKey != null;
                    assert apiKey.getKey() != null;
                    assert apiKey.getServices() != null;
                    assert apiKey.getServices().contains(serviceName);
                });
    }

    @Test
    void testGetApiKey() {
        String apiKeyValue = UUID.randomUUID().toString();
        ApiKey apiKey = new ApiKey(apiKeyValue, List.of("test-service"));
        redisHashProcessor.setHashValue(RECORD_KEY, apiKey.getKey(), apiKey);

        webTestClient.get()
                .uri("/{apiKey}", apiKeyValue)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ApiKey.class)
                .value(apiKeyResponse -> {
                    assert apiKeyResponse != null;
                    assert apiKeyResponse.getKey() != null;
                    assert apiKeyResponse.getKey().equals(apiKeyValue);
                    assert apiKeyResponse.getServices() != null;
                    assert apiKeyResponse.getServices().contains("test-service");
                });
    }
}
