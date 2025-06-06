package com.ahamo.plan.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.DecimalMin;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminPlanRequest {
    
    @NotBlank(message = "Plan name is required")
    private String name;
    
    private String description;
    
    @NotNull(message = "Monthly fee is required")
    @DecimalMin(value = "0.0", message = "Monthly fee must be positive")
    private BigDecimal monthlyFee;
    
    @NotBlank(message = "Data capacity is required")
    private String dataCapacity;
    
    @NotBlank(message = "Voice calls is required")
    private String voiceCalls;
    
    private LocalDateTime effectiveStartDate;
    private LocalDateTime effectiveEndDate;
    
    private LocalDateTime campaignStartDate;
    private LocalDateTime campaignEndDate;
    
    @NotBlank(message = "Change reason is required")
    private String changeReason;
}
