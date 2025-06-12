package com.ahamo.payment.security;

import com.ahamo.payment.gateway.dto.CardDetails;
import com.ahamo.payment.gateway.dto.TokenizationResult;

public interface TokenizationService {
    
    TokenizationResult tokenizeCard(CardDetails cardDetails);
    
    boolean validateToken(String token);
    
    String detokenize(String token);
    
    void revokeToken(String token);
}
