package com.ahamo.payment.security;

import com.ahamo.payment.audit.PaymentAuditService;
import com.ahamo.payment.gateway.dto.CardDetails;
import com.ahamo.payment.gateway.dto.TokenizationResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import org.mockito.Mockito;

@ExtendWith(MockitoExtension.class)
class TokenizationServiceImplTest {

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    private TokenizationServiceImpl tokenizationService;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        PaymentAuditService mockAuditService = Mockito.mock(PaymentAuditService.class);
        tokenizationService = new TokenizationServiceImpl();
        
        ReflectionTestUtils.setField(tokenizationService, "redisTemplate", redisTemplate);
        ReflectionTestUtils.setField(tokenizationService, "objectMapper", objectMapper);
        ReflectionTestUtils.setField(tokenizationService, "auditService", mockAuditService);
        ReflectionTestUtils.setField(tokenizationService, "tokenTtl", 3600L);
        ReflectionTestUtils.setField(tokenizationService, "encryptionKey", "ahamo_contract_form_key_2024_32b");
    }

    @Test
    void testTokenizeCard_Success() {
        CardDetails cardDetails = CardDetails.builder()
            .cardNumber("4111111111111111")
            .expiryMonth("12")
            .expiryYear("2025")
            .cvv("123")
            .cardHolderName("TARO YAMADA")
            .build();
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        TokenizationResult result = tokenizationService.tokenizeCard(cardDetails);

        assertTrue(result.isSuccess());
        assertNotNull(result.getToken());
        assertEquals("**** **** **** 1111", result.getMaskedCardNumber());
        assertEquals("visa", result.getCardType());
        
        verify(valueOperations).set(anyString(), anyString(), any());
    }

    @Test
    void testValidateToken_ValidToken() {
        String token = "test_token";
        when(redisTemplate.hasKey("payment_token:" + token)).thenReturn(true);

        boolean result = tokenizationService.validateToken(token);

        assertTrue(result);
    }

    @Test
    void testValidateToken_InvalidToken() {
        String token = "invalid_token";
        when(redisTemplate.hasKey("payment_token:" + token)).thenReturn(false);

        boolean result = tokenizationService.validateToken(token);

        assertFalse(result);
    }

    @Test
    void testRevokeToken() {
        String token = "test_token";

        tokenizationService.revokeToken(token);

        verify(redisTemplate).delete("payment_token:" + token);
    }
}
