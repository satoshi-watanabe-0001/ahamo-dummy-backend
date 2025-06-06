package com.ahamo.option.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OptionResponse {
    
    private String id;
    private String name;
    private String category;
    private String description;
    private BigDecimal monthlyFee;
    private BigDecimal oneTimeFee;
    private Boolean isActive;
    private LocalDateTime effectiveStartDate;
    private LocalDateTime effectiveEndDate;
    
    private String createdBy;
    private String updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    private List<String> requiredOptions;
    private List<String> excludedOptions;
}
