package com.ahamo.pricing.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.DecimalMin;
import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PricingCalculationRequest {
    
    @NotBlank(message = "Plan ID is required")
    private String planId;
    
    @NotBlank(message = "Device ID is required")
    private String deviceId;
    
    private String deviceColor;
    private String deviceStorage;
    
    @NotNull(message = "Payment option is required")
    private DevicePaymentOption paymentOption;
    
    private String tradeInDeviceId;
    
    private List<String> selectedOptionIds;
    
    @NotNull(message = "Data usage is required")
    @DecimalMin(value = "0.0", message = "Data usage must be positive")
    private BigDecimal dataUsage;
    
    @NotNull(message = "Call minutes is required")
    @DecimalMin(value = "0.0", message = "Call minutes must be positive")
    private BigDecimal callMinutes;
    
    @NotNull(message = "SMS count is required")
    @DecimalMin(value = "0.0", message = "SMS count must be positive")
    private BigDecimal smsCount;
    
    private boolean isNewCustomer;
    private boolean isMnpTransfer;
    
    public enum DevicePaymentOption {
        LUMP_SUM, INSTALLMENT_24, INSTALLMENT_36
    }
}
