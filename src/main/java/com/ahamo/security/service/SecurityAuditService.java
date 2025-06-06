package com.ahamo.security.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class SecurityAuditService {

    private final RedisTemplate<String, String> redisTemplate;

    @Value("${security.rate-limit.login-attempts:5}")
    private int maxLoginAttempts;

    @Value("${security.rate-limit.time-window:300000}")
    private long timeWindow;

    private static final String LOGIN_ATTEMPTS_PREFIX = "login_attempts:";
    private static final String SECURITY_EVENT_PREFIX = "security_event:";
    private static final String IP_BLACKLIST_PREFIX = "ip_blacklist:";

    public boolean isIpBlocked(String ipAddress) {
        String key = IP_BLACKLIST_PREFIX + ipAddress;
        return redisTemplate.hasKey(key);
    }

    public boolean checkRateLimit(String ipAddress) {
        String key = LOGIN_ATTEMPTS_PREFIX + ipAddress;
        String attempts = redisTemplate.opsForValue().get(key);
        
        if (attempts == null) {
            redisTemplate.opsForValue().set(key, "1", timeWindow, TimeUnit.MILLISECONDS);
            return true;
        }
        
        int currentAttempts = Integer.parseInt(attempts);
        if (currentAttempts >= maxLoginAttempts) {
            blockIpAddress(ipAddress);
            logSecurityEvent("RATE_LIMIT_EXCEEDED", ipAddress, "IP blocked due to excessive login attempts");
            return false;
        }
        
        redisTemplate.opsForValue().increment(key);
        return true;
    }

    public void resetRateLimit(String ipAddress) {
        String key = LOGIN_ATTEMPTS_PREFIX + ipAddress;
        redisTemplate.delete(key);
    }

    public void blockIpAddress(String ipAddress) {
        String key = IP_BLACKLIST_PREFIX + ipAddress;
        redisTemplate.opsForValue().set(key, "blocked", 24, TimeUnit.HOURS);
        log.warn("IP address blocked: {}", ipAddress);
    }

    public void logSecurityEvent(String eventType, String ipAddress, String details) {
        String eventKey = SECURITY_EVENT_PREFIX + System.currentTimeMillis();
        
        Map<String, String> eventData = new HashMap<>();
        eventData.put("event_type", eventType);
        eventData.put("ip_address", ipAddress);
        eventData.put("details", details);
        eventData.put("timestamp", LocalDateTime.now().toString());
        
        redisTemplate.opsForHash().putAll(eventKey, eventData);
        redisTemplate.expire(eventKey, 30, TimeUnit.DAYS);
        
        log.warn("Security event logged: {} from IP: {} - {}", eventType, ipAddress, details);
    }

    public void logFailedLogin(String email, String ipAddress) {
        logSecurityEvent("FAILED_LOGIN", ipAddress, "Failed login attempt for email: " + email);
    }

    public void logSuccessfulLogin(String email, String ipAddress) {
        logSecurityEvent("SUCCESSFUL_LOGIN", ipAddress, "Successful login for email: " + email);
        resetRateLimit(ipAddress);
    }

    public void logJwtTampering(String ipAddress, String token) {
        logSecurityEvent("JWT_TAMPERING", ipAddress, "Invalid JWT token detected");
    }

    public void logUnauthorizedAccess(String ipAddress, String endpoint) {
        logSecurityEvent("UNAUTHORIZED_ACCESS", ipAddress, "Unauthorized access attempt to: " + endpoint);
    }

    public void logPersonalDataAccess(String eventType, String ipAddress, String details, Long customerId) {
        String eventKey = SECURITY_EVENT_PREFIX + System.currentTimeMillis();
        
        Map<String, String> eventData = new HashMap<>();
        eventData.put("event_type", eventType);
        eventData.put("ip_address", ipAddress);
        eventData.put("details", details);
        eventData.put("timestamp", LocalDateTime.now().toString());
        eventData.put("data_type", "PERSONAL_DATA");
        if (customerId != null) {
            eventData.put("customer_id", customerId.toString());
        }
        
        redisTemplate.opsForHash().putAll(eventKey, eventData);
        redisTemplate.expire(eventKey, 30, TimeUnit.DAYS);
        
        log.info("Personal data access logged: {} from IP: {} - {}", eventType, ipAddress, details);
    }

    public void logPersonalDataChange(String eventType, String ipAddress, String details, Long customerId) {
        String eventKey = SECURITY_EVENT_PREFIX + System.currentTimeMillis();
        
        Map<String, String> eventData = new HashMap<>();
        eventData.put("event_type", eventType);
        eventData.put("ip_address", ipAddress);
        eventData.put("details", details);
        eventData.put("timestamp", LocalDateTime.now().toString());
        eventData.put("data_type", "PERSONAL_DATA_CHANGE");
        if (customerId != null) {
            eventData.put("customer_id", customerId.toString());
        }
        
        redisTemplate.opsForHash().putAll(eventKey, eventData);
        redisTemplate.expire(eventKey, 30, TimeUnit.DAYS);
        
        log.warn("Personal data change logged: {} from IP: {} - {}", eventType, ipAddress, details);
    }

    public String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
}
