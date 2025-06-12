package com.ahamo.plan.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.DecimalMin;
import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeeCalculationRequest {
    
    @NotBlank(message = "Plan ID is required")
    private String planId;
    
    @NotNull(message = "Data usage is required")
    @DecimalMin(value = "0.0", message = "Data usage must be positive")
    private BigDecimal dataUsage;
    
    @NotNull(message = "Call minutes is required")
    @DecimalMin(value = "0.0", message = "Call minutes must be positive")
    private BigDecimal callMinutes;
    
    @NotNull(message = "SMS count is required")
    @DecimalMin(value = "0.0", message = "SMS count must be positive")
    private BigDecimal smsCount;
    
    private List<String> selectedOptionIds;
}
