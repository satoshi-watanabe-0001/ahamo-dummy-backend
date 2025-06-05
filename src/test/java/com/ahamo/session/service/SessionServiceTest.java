package com.ahamo.session.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class SessionServiceTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @InjectMocks
    private SessionService sessionService;

    private Map<String, Object> testSessionData;

    @BeforeEach
    void setUp() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        
        testSessionData = new HashMap<>();
        testSessionData.put("token", "test-token");
        testSessionData.put("created_at", System.currentTimeMillis());
        testSessionData.put("last_accessed", System.currentTimeMillis());
    }

    @Test
    void createSession_ValidData_CreatesSession() {
        String userId = "1";
        String token = "test-token";

        sessionService.createSession(userId, token);

        verify(valueOperations).set(eq("session:" + userId), any(), anyLong(), eq(TimeUnit.MILLISECONDS));
    }

    @Test
    void isSessionValid_ValidSession_ReturnsTrue() {
        String userId = "1";
        when(valueOperations.get("session:" + userId)).thenReturn(testSessionData);

        boolean result = sessionService.isSessionValid(userId);

        assertTrue(result);
        verify(valueOperations, atLeastOnce()).get("session:" + userId);
    }

    @Test
    void isSessionValid_InvalidSession_ReturnsFalse() {
        String userId = "999";
        when(valueOperations.get("session:" + userId)).thenReturn(null);

        boolean result = sessionService.isSessionValid(userId);

        assertFalse(result);
        verify(valueOperations).get("session:" + userId);
    }

    @Test
    void invalidateSession_ValidUserId_InvalidatesSession() {
        String userId = "1";

        sessionService.invalidateSession(userId);

        verify(redisTemplate).delete("session:" + userId);
    }

    @Test
    void storeContractData_ValidData_StoresData() {
        String userId = "1";
        Map<String, Object> contractData = new HashMap<>();
        contractData.put("contractNumber", "C001234567");

        sessionService.storeContractData(userId, contractData);

        verify(valueOperations).set(eq("contract_data:" + userId), any(), eq(86400000L), eq(TimeUnit.MILLISECONDS));
    }

    @Test
    void getContractData_ExistingData_ReturnsData() {
        String userId = "1";
        when(valueOperations.get("contract_data:" + userId)).thenReturn(testSessionData);

        Map<String, Object> result = sessionService.getContractData(userId);

        assertEquals(testSessionData, result);
        verify(valueOperations).get("contract_data:" + userId);
    }

    @Test
    void getContractData_NoData_ReturnsNull() {
        String userId = "999";
        when(valueOperations.get("contract_data:" + userId)).thenReturn(null);

        Map<String, Object> result = sessionService.getContractData(userId);

        assertNull(result);
        verify(valueOperations).get("contract_data:" + userId);
    }

    @Test
    void clearContractData_ValidUserId_ClearsData() {
        String userId = "1";

        sessionService.clearContractData(userId);

        verify(redisTemplate).delete("contract_data:" + userId);
    }

    @Test
    void extendContractDataTtl_ValidUserId_ExtendsExpiration() {
        String userId = "1";

        sessionService.extendContractDataTtl(userId);

        verify(redisTemplate).expire("contract_data:" + userId, 86400000L, TimeUnit.MILLISECONDS);
    }
}
