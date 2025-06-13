package com.ahamo.payment.security;

import com.ahamo.payment.gateway.dto.CardDetails;
import com.ahamo.payment.gateway.dto.TokenizationResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.ahamo.payment.audit.PaymentAuditService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

import java.time.Duration;
import java.util.Base64;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
@Slf4j
@RequiredArgsConstructor
public class TokenizationServiceImpl implements TokenizationService {
    
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;
    private final PaymentAuditService auditService;
    
    @Value("${payment.tokenization.ttl:3600}")
    private long tokenTtl;
    
    @Value("${payment.tokenization.key:ahamo_contract_form_key_2024}")
    private String encryptionKey;
    
    private static final String TOKEN_PREFIX = "payment_token:";
    private static final String ALGORITHM = "AES";
    
    @Override
    public TokenizationResult tokenizeCard(CardDetails cardDetails) {
        try {
            String token = generateSecureToken();
            String encryptedCardData = encrypt(objectMapper.writeValueAsString(cardDetails));
            
            redisTemplate.opsForValue().set(
                TOKEN_PREFIX + token, 
                encryptedCardData, 
                Duration.ofSeconds(tokenTtl)
            );
            
            String maskedCardNumber = maskCardNumber(cardDetails.getCardNumber());
            String cardType = detectCardType(cardDetails.getCardNumber());
            
            auditService.logTokenizationEvent("TOKENIZE_SUCCESS", maskedCardNumber, true);
            
            return TokenizationResult.builder()
                .success(true)
                .token(token)
                .maskedCardNumber(maskedCardNumber)
                .cardType(cardType)
                .build();
        } catch (Exception e) {
            log.error("Tokenization failed", e);
            auditService.logTokenizationEvent("TOKENIZE_FAILED", "****", false);
            return TokenizationResult.builder()
                .success(false)
                .errorMessage("カードのトークン化に失敗しました")
                .build();
        }
    }
    
    @Override
    public boolean validateToken(String token) {
        try {
            return redisTemplate.hasKey(TOKEN_PREFIX + token);
        } catch (Exception e) {
            log.error("Token validation failed", e);
            return false;
        }
    }
    
    @Override
    public String detokenize(String token) {
        try {
            String encryptedData = redisTemplate.opsForValue().get(TOKEN_PREFIX + token);
            if (encryptedData == null) {
                throw new IllegalArgumentException("Invalid or expired token");
            }
            return decrypt(encryptedData);
        } catch (Exception e) {
            log.error("Detokenization failed", e);
            throw new RuntimeException("トークンの復号化に失敗しました");
        }
    }
    
    @Override
    public void revokeToken(String token) {
        try {
            redisTemplate.delete(TOKEN_PREFIX + token);
            log.info("Token revoked successfully");
        } catch (Exception e) {
            log.error("Token revocation failed", e);
        }
    }
    
    private String generateSecureToken() {
        return "tok_" + UUID.randomUUID().toString().replace("-", "") + 
               System.currentTimeMillis();
    }
    
    private String encrypt(String data) throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec(
            encryptionKey.getBytes(StandardCharsets.UTF_8), ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedData = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encryptedData);
    }
    
    private String decrypt(String encryptedData) throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec(
            encryptionKey.getBytes(StandardCharsets.UTF_8), ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decodedData = Base64.getDecoder().decode(encryptedData);
        byte[] decryptedData = cipher.doFinal(decodedData);
        return new String(decryptedData, StandardCharsets.UTF_8);
    }
    
    private String maskCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 4) {
            return "****";
        }
        String cleanNumber = cardNumber.replaceAll("\\s+", "");
        return "**** **** **** " + cleanNumber.substring(cleanNumber.length() - 4);
    }
    
    private String detectCardType(String cardNumber) {
        String cleanNumber = cardNumber.replaceAll("\\s+", "");
        
        if (Pattern.matches("^4[0-9]{12}(?:[0-9]{3})?$", cleanNumber)) {
            return "visa";
        } else if (Pattern.matches("^5[1-5][0-9]{14}$", cleanNumber)) {
            return "mastercard";
        } else if (Pattern.matches("^35(2[89]|[3-8][0-9])[0-9]{12}$", cleanNumber)) {
            return "jcb";
        } else if (Pattern.matches("^3[47][0-9]{13}$", cleanNumber)) {
            return "amex";
        }
        return "unknown";
    }
}
