package com.example.tms.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RefreshTokenRedisService {

    private static final Logger log = LoggerFactory.getLogger(RefreshTokenRedisService.class);

    private final RedisTemplate<String, String> redisTemplate;
    private final String keyPrefix;

    public RefreshTokenRedisService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.keyPrefix = "tms:";
    }

    private String blacklistKey(String tokenHash) {
        return keyPrefix + "rt:blacklist:" + tokenHash;
    }

    public void blacklist(String tokenHash, Duration ttl) {
        if (ttl.isNegative() || ttl.isZero()) {
            return;
        }
        try {
            redisTemplate.opsForValue().set(blacklistKey(tokenHash), "1", ttl);
        } catch (Exception ex) {
            log.warn("Failed to store refresh token blacklist entry in Redis: {}", ex.getMessage());
        }
    }

    public boolean isBlacklisted(String tokenHash) {
        try {
            String value = redisTemplate.opsForValue().get(blacklistKey(tokenHash));
            return value != null;
        } catch (Exception ex) {
            log.warn("Failed to check refresh token blacklist in Redis: {}", ex.getMessage());
            return false;
        }
    }
}

