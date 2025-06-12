package com.ahamo.payment.gateway.adapter;

import com.ahamo.payment.gateway.PaymentGatewayAdapter;
import com.ahamo.payment.gateway.dto.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
public class MockPaymentGatewayAdapter implements PaymentGatewayAdapter {
    
    @Override
    public GatewayResponse processPayment(GatewayRequest request) {
        log.info("Processing mock payment for amount: {}", request.getAmount());
        
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        boolean success = Math.random() > 0.1;
        
        return GatewayResponse.builder()
                .success(success)
                .transactionId(UUID.randomUUID().toString())
                .status(success ? "completed" : "failed")
                .message(success ? "Payment processed successfully" : "Payment failed")
                .build();
    }
    
    @Override
    public TokenizationResult tokenize(CardDetails cardDetails) {
        log.info("Tokenizing card for holder: {}", cardDetails.getCardHolderName());
        
        String token = "tok_" + UUID.randomUUID().toString().replace("-", "");
        String maskedCard = "**** **** **** " + cardDetails.getCardNumber().substring(cardDetails.getCardNumber().length() - 4);
        
        return TokenizationResult.builder()
                .success(true)
                .token(token)
                .maskedCardNumber(maskedCard)
                .cardType("visa")
                .build();
    }
    
    @Override
    public boolean validatePayment(PaymentValidationRequest request) {
        log.info("Validating payment: {}", request.getPaymentId());
        return true;
    }
    
    @Override
    public String getGatewayName() {
        return "Mock Payment Gateway";
    }
    
    @Override
    public boolean isAvailable() {
        return true;
    }
}
