package com.ahamo.payment.gateway.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentValidationRequest {
    private String paymentId;
    private String transactionId;
    private BigDecimal amount;
    private String paymentMethodId;
}
