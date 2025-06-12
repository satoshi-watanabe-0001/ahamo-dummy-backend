package com.ahamo.payment.gateway.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenizationResult {
    private boolean success;
    private String token;
    private String maskedCardNumber;
    private String cardType;
    private String errorMessage;
}
