package com.hrms.common.security;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TokenBlacklistService {

    private final Map<String, Long> blacklistedTokens = new ConcurrentHashMap<>();

    public void addToBlacklist(String token, long expirationTime) {
        blacklistedTokens.put(token, expirationTime);
    }

    public boolean isBlacklisted(String token) {
        Long expiry = blacklistedTokens.get(token);
        if (expiry != null && expiry > System.currentTimeMillis()) {
            return true;
        }
        blacklistedTokens.remove(token);
        return false;
    }

    public void cleanupExpiredTokens() {
        blacklistedTokens.entrySet().removeIf(entry -> entry.getValue() <= System.currentTimeMillis());
    }
}
