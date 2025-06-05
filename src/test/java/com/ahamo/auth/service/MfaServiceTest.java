package com.ahamo.auth.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class MfaServiceTest {

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private com.ahamo.user.service.UserService userService;

    @InjectMocks
    private MfaService mfaService;

    @BeforeEach
    void setUp() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    void generateVerificationCode_ReturnsValidCode() {
        String code = mfaService.generateAndSendEmailVerification("test@example.com");

        assertNotNull(code);
        assertEquals(6, code.length());
        assertTrue(code.matches("\\d{6}"));
    }

    @Test
    void generateAndSendEmailVerification_ValidEmail_ReturnsCode() {
        String email = "test@example.com";

        String result = mfaService.generateAndSendEmailVerification(email);

        assertNotNull(result);
        assertEquals(6, result.length());
        assertTrue(result.matches("\\d{6}"));
    }

    @Test
    void generateAndSendSmsVerification_ValidPhoneNumber_ReturnsCode() {
        String phoneNumber = "090-1234-5678";

        String result = mfaService.generateAndSendSmsVerification(phoneNumber);

        assertNotNull(result);
        assertEquals(6, result.length());
    }

    @Test
    void verifyEmailCode_ValidCode_ReturnsTrue() {
        String email = "test@example.com";
        String code = "123456";
        when(valueOperations.get("email_code:" + email)).thenReturn(code);

        boolean result = mfaService.verifyEmailCode(email, code);

        assertTrue(result);
    }

    @Test
    void verifyEmailCode_InvalidCode_ReturnsFalse() {
        String email = "test@example.com";
        String code = "123456";
        String wrongCode = "654321";
        when(valueOperations.get("email_code:" + email)).thenReturn(code);

        boolean result = mfaService.verifyEmailCode(email, wrongCode);

        assertFalse(result);
        verify(redisTemplate, never()).delete("email_code:" + email);
    }

    @Test
    void verifyEmailCode_ExpiredCode_ReturnsFalse() {
        String email = "test@example.com";
        String code = "123456";
        when(valueOperations.get("email_code:" + email)).thenReturn(null);

        boolean result = mfaService.verifyEmailCode(email, code);

        assertFalse(result);
        verify(redisTemplate, never()).delete("email_code:" + email);
    }

    @Test
    void verifySmsCode_ValidCode_ReturnsTrue() {
        String phoneNumber = "090-1234-5678";
        String code = "123456";
        when(valueOperations.get("sms_code:" + phoneNumber)).thenReturn(code);

        boolean result = mfaService.verifySmsCode(phoneNumber, code);

        assertTrue(result);
    }

    @Test
    void verifySmsCode_InvalidCode_ReturnsFalse() {
        String phoneNumber = "090-1234-5678";
        String code = "123456";
        String wrongCode = "654321";
        when(valueOperations.get("sms_code:" + phoneNumber)).thenReturn(code);

        boolean result = mfaService.verifySmsCode(phoneNumber, wrongCode);

        assertFalse(result);
        verify(redisTemplate, never()).delete("sms_code:" + phoneNumber);
    }

    @Test
    void isCodeExpired_ExpiredCode_ReturnsTrue() {
        String email = "test@example.com";
        when(valueOperations.get("email_code:" + email)).thenReturn(null);

        boolean result = !mfaService.verifyEmailCode(email, "123456");

        assertTrue(result);
    }

    @Test
    void isCodeExpired_ValidCode_ReturnsFalse() {
        String email = "test@example.com";
        when(valueOperations.get("email_code:" + email)).thenReturn("123456");

        boolean result = mfaService.verifyEmailCode(email, "123456");

        assertTrue(result);
    }


}
