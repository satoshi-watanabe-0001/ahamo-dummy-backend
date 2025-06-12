package com.ahamo.payment.service;

import com.ahamo.payment.gateway.dto.CardDetails;
import com.ahamo.payment.gateway.dto.TokenizationResult;
import com.ahamo.payment.security.TokenizationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class TokenizationServiceImpl implements TokenizationService {
    
    private final SecureRandom secureRandom = new SecureRandom();
    private final ConcurrentHashMap<String, String> tokenStore = new ConcurrentHashMap<>();
    
    @Override
    public TokenizationResult tokenizeCard(CardDetails cardDetails) {
        log.info("Tokenizing card for holder: {}", cardDetails.getCardHolderName());
        
        try {
            String token = generateSecureToken();
            String maskedCardNumber = maskCardNumber(cardDetails.getCardNumber());
            
            tokenStore.put(token, cardDetails.getCardNumber());
            
            return TokenizationResult.builder()
                    .success(true)
                    .token(token)
                    .maskedCardNumber(maskedCardNumber)
                    .cardType(detectCardType(cardDetails.getCardNumber()))
                    .build();
                    
        } catch (Exception e) {
            log.error("Card tokenization failed", e);
            return TokenizationResult.builder()
                    .success(false)
                    .errorMessage("Tokenization failed: " + e.getMessage())
                    .build();
        }
    }
    
    @Override
    public boolean validateToken(String token) {
        return tokenStore.containsKey(token);
    }
    
    @Override
    public String detokenize(String token) {
        return tokenStore.get(token);
    }
    
    @Override
    public void revokeToken(String token) {
        tokenStore.remove(token);
        log.info("Token revoked: {}", token.substring(0, 8) + "...");
    }
    
    private String generateSecureToken() {
        byte[] tokenBytes = new byte[32];
        secureRandom.nextBytes(tokenBytes);
        return "tok_" + Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
    }
    
    private String maskCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 4) {
            return "****";
        }
        
        String lastFour = cardNumber.substring(cardNumber.length() - 4);
        return "**** **** **** " + lastFour;
    }
    
    private String detectCardType(String cardNumber) {
        if (cardNumber.startsWith("4")) {
            return "visa";
        } else if (cardNumber.startsWith("5")) {
            return "mastercard";
        } else if (cardNumber.startsWith("3")) {
            return "amex";
        }
        return "unknown";
    }
}
