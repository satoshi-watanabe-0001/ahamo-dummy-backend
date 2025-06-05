package com.ahamo.security.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class SecurityAuditServiceTest {

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private SecurityAuditService securityAuditService;

    @BeforeEach
    void setUp() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(redisTemplate.opsForHash()).thenReturn(mock(org.springframework.data.redis.core.HashOperations.class));
    }

    @Test
    void checkRateLimit_FirstAttempt_ReturnsTrue() {
        String ipAddress = "192.168.1.1";
        when(valueOperations.get("login_attempts:" + ipAddress)).thenReturn(null);

        boolean result = securityAuditService.checkRateLimit(ipAddress);

        assertTrue(result);
        verify(valueOperations).set(eq("login_attempts:" + ipAddress), eq("1"), anyLong(), eq(TimeUnit.MILLISECONDS));
    }

    @Test
    void isIpBlocked_BlockedIp_ReturnsTrue() {
        String ipAddress = "192.168.1.1";
        when(valueOperations.get("blocked_ip:" + ipAddress)).thenReturn("blocked");

        boolean result = securityAuditService.isIpBlocked(ipAddress);

        assertTrue(result);
        verify(valueOperations).get("blocked_ip:" + ipAddress);
    }

    @Test
    void isIpBlocked_NonBlockedIp_ReturnsFalse() {
        String ipAddress = "192.168.1.1";
        when(valueOperations.get("blocked_ip:" + ipAddress)).thenReturn(null);

        boolean result = securityAuditService.isIpBlocked(ipAddress);

        assertFalse(result);
        verify(valueOperations).get("blocked_ip:" + ipAddress);
    }

    @Test
    void blockIpAddress_ValidIp_BlocksIp() {
        String ipAddress = "192.168.1.1";

        securityAuditService.blockIpAddress(ipAddress);

        verify(valueOperations).set(eq("ip_blacklist:" + ipAddress), eq("blocked"), eq(24L), eq(TimeUnit.HOURS));
    }

    @Test
    void resetRateLimit_ValidIp_ResetsCounter() {
        String ipAddress = "192.168.1.1";

        securityAuditService.resetRateLimit(ipAddress);

        verify(redisTemplate).delete("login_attempts:" + ipAddress);
    }

    @Test
    void logSecurityEvent_ValidEvent_LogsEvent() {
        String eventType = "SUSPICIOUS_LOGIN";
        String ipAddress = "192.168.1.1";
        String details = "Multiple failed attempts";

        securityAuditService.logSecurityEvent(eventType, ipAddress, details);

        verify(redisTemplate).opsForHash();
    }

    @Test
    void logFailedLogin_ValidData_LogsEvent() {
        String email = "test@example.com";
        String ipAddress = "192.168.1.1";

        securityAuditService.logFailedLogin(email, ipAddress);

        verify(redisTemplate).opsForHash();
    }

    @Test
    void logSuccessfulLogin_ValidData_LogsEventAndResetsRateLimit() {
        String email = "test@example.com";
        String ipAddress = "192.168.1.1";

        securityAuditService.logSuccessfulLogin(email, ipAddress);

        verify(redisTemplate).opsForHash();
        verify(redisTemplate).delete("login_attempts:" + ipAddress);
    }

    @Test
    void getClientIpAddress_ValidRequest_ReturnsIpAddress() {
        String expectedIp = "192.168.1.1";
        when(request.getRemoteAddr()).thenReturn(expectedIp);

        String result = securityAuditService.getClientIpAddress(request);

        assertEquals(expectedIp, result);
        verify(request).getRemoteAddr();
    }

    @Test
    void getClientIpAddress_ProxiedRequest_ReturnsForwardedIp() {
        String forwardedIp = "10.0.0.1";
        when(request.getHeader("X-Forwarded-For")).thenReturn(forwardedIp);

        String result = securityAuditService.getClientIpAddress(request);

        assertEquals(forwardedIp, result);
        verify(request).getHeader("X-Forwarded-For");
    }
}
