package com.ahamo.payment.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class PaymentMethodResponseDto {
    private String id;
    private String name;
    private String type;
    private String description;
    private boolean isAvailable;
    private BigDecimal additionalFees;
    private String icon;
}
