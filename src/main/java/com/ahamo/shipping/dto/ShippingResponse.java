package com.ahamo.shipping.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShippingResponse {

    private Long orderId;

    private String orderNumber;

    private String trackingNumber;

    private String status;

    private LocalDate estimatedDeliveryDate;

    private String providerCode;

    private String providerName;

    private String message;

    private LocalDateTime createdAt;
}
