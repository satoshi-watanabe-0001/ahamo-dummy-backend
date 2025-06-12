package com.ahamo.payment.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentMethodDto {
    private String id;
    private String name;
    private String type;
    private boolean isAvailable;
    private BigDecimal additionalFees;
}
