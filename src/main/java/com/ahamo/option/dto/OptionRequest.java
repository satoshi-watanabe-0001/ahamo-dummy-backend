package com.ahamo.option.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.DecimalMin;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OptionRequest {
    
    @NotBlank(message = "Option name is required")
    private String name;
    
    @NotBlank(message = "Category is required")
    private String category;
    
    private String description;
    
    @NotNull(message = "Monthly fee is required")
    @DecimalMin(value = "0.0", message = "Monthly fee must be positive")
    private BigDecimal monthlyFee;
    
    @DecimalMin(value = "0.0", message = "One time fee must be positive")
    private BigDecimal oneTimeFee;
    
    private Boolean isActive;
    
    private LocalDateTime effectiveStartDate;
    
    private LocalDateTime effectiveEndDate;
    
    private List<String> requiredOptions;
    
    private List<String> excludedOptions;
}
