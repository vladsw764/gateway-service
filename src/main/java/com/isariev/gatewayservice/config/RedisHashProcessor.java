package com.isariev.gatewayservice.config;

import com.isariev.gatewayservice.util.MapperUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
public class RedisHashProcessor {
    private final RedisTemplate<String, Object> redisTemplate;

    public RedisHashProcessor(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void setHashValue(String key, Object hashKey, Object value) {
        Map hash = MapperUtils.objectMapper(value, Map.class);
        redisTemplate.opsForHash().put(key, hashKey, hash);
    }

    public Object getHashValue(String key, Object hashKey) {
        return redisTemplate.opsForHash().get(key, hashKey);
    }

    public void deleteHash(String key) {
        redisTemplate.delete(key);
    }
}
