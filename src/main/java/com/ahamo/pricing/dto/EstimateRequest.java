package com.ahamo.pricing.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EstimateRequest {
    
    @NotBlank(message = "Plan ID is required")
    private String planId;
    
    @NotBlank(message = "Device ID is required")
    private String deviceId;
    
    private String deviceColor;
    private String deviceStorage;
    
    @NotNull(message = "Payment option is required")
    private PricingCalculationRequest.DevicePaymentOption paymentOption;
    
    private String tradeInDeviceId;
    
    private List<String> selectedOptionIds;
    
    private boolean isNewCustomer;
    private boolean isMnpTransfer;
    
    @NotNull(message = "Estimate period is required")
    private Integer estimatePeriodMonths;
}
