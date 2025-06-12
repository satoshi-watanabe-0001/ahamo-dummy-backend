package com.ahamo.payment.gateway.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GatewayResponse {
    private boolean success;
    private String transactionId;
    private String status;
    private String message;
    private String redirectUrl;
    private String errorCode;
}
