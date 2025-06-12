package com.ahamo.payment.gateway.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GatewayRequest {
    private String paymentId;
    private BigDecimal amount;
    private String currency;
    private String paymentMethodId;
    private String customerToken;
    private Map<String, Object> metadata;
}
