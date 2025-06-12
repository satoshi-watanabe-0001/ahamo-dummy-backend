package com.ahamo.payment.gateway;

import com.ahamo.payment.gateway.dto.GatewayRequest;
import com.ahamo.payment.gateway.dto.GatewayResponse;
import com.ahamo.payment.gateway.dto.CardDetails;
import com.ahamo.payment.gateway.dto.TokenizationResult;
import com.ahamo.payment.gateway.dto.PaymentValidationRequest;

public interface PaymentGatewayAdapter {
    
    GatewayResponse processPayment(GatewayRequest request);
    
    TokenizationResult tokenize(CardDetails cardDetails);
    
    boolean validatePayment(PaymentValidationRequest request);
    
    String getGatewayName();
    
    boolean isAvailable();
}
