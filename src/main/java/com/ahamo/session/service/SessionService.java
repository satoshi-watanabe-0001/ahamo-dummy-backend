package com.ahamo.session.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class SessionService {

    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${security.session.timeout:86400000}")
    private long sessionTimeout;

    private static final String SESSION_PREFIX = "session:";
    private static final String CONTRACT_DATA_PREFIX = "contract_data:";
    private static final long CONTRACT_DATA_TTL = 24 * 60 * 60 * 1000;

    public void createSession(String userId, String token) {
        String sessionKey = SESSION_PREFIX + userId;
        
        Map<String, Object> sessionData = new HashMap<>();
        sessionData.put("token", token);
        sessionData.put("created_at", System.currentTimeMillis());
        sessionData.put("last_accessed", System.currentTimeMillis());
        
        redisTemplate.opsForValue().set(sessionKey, sessionData, sessionTimeout, TimeUnit.MILLISECONDS);
        log.info("Session created for user: {}", userId);
    }

    public boolean isSessionValid(String userId) {
        String sessionKey = SESSION_PREFIX + userId;
        Object sessionData = redisTemplate.opsForValue().get(sessionKey);
        
        if (sessionData != null) {
            updateLastAccessed(userId);
            return true;
        }
        
        return false;
    }

    public void invalidateSession(String userId) {
        String sessionKey = SESSION_PREFIX + userId;
        redisTemplate.delete(sessionKey);
        log.info("Session invalidated for user: {}", userId);
    }

    public void updateLastAccessed(String userId) {
        String sessionKey = SESSION_PREFIX + userId;
        Object sessionData = redisTemplate.opsForValue().get(sessionKey);
        
        if (sessionData instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> session = (Map<String, Object>) sessionData;
            session.put("last_accessed", System.currentTimeMillis());
            redisTemplate.opsForValue().set(sessionKey, session, sessionTimeout, TimeUnit.MILLISECONDS);
        }
    }

    public void storeContractData(String userId, Map<String, Object> contractData) {
        String contractKey = CONTRACT_DATA_PREFIX + userId;
        
        Map<String, Object> dataWithTimestamp = new HashMap<>(contractData);
        dataWithTimestamp.put("stored_at", System.currentTimeMillis());
        
        redisTemplate.opsForValue().set(contractKey, dataWithTimestamp, CONTRACT_DATA_TTL, TimeUnit.MILLISECONDS);
        log.info("Contract data stored for user: {}", userId);
    }

    public Map<String, Object> getContractData(String userId) {
        String contractKey = CONTRACT_DATA_PREFIX + userId;
        Object data = redisTemplate.opsForValue().get(contractKey);
        
        if (data instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> contractData = (Map<String, Object>) data;
            log.info("Contract data retrieved for user: {}", userId);
            return contractData;
        }
        
        return null;
    }

    public void clearContractData(String userId) {
        String contractKey = CONTRACT_DATA_PREFIX + userId;
        redisTemplate.delete(contractKey);
        log.info("Contract data cleared for user: {}", userId);
    }

    public void extendContractDataTtl(String userId) {
        String contractKey = CONTRACT_DATA_PREFIX + userId;
        redisTemplate.expire(contractKey, CONTRACT_DATA_TTL, TimeUnit.MILLISECONDS);
        log.info("Contract data TTL extended for user: {}", userId);
    }
}
