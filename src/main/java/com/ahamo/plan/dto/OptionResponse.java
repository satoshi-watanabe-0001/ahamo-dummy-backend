package com.ahamo.plan.dto;

import com.ahamo.option.model.Option;
import lombok.Data;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class OptionResponse {
    private String id;
    private String name;
    private String category;
    private String description;
    private BigDecimal monthlyFee;
    private BigDecimal oneTimeFee;
    private boolean isActive;
    private LocalDateTime effectiveStartDate;
    private LocalDateTime effectiveEndDate;
    
    public static OptionResponse fromOption(Option option) {
        return OptionResponse.builder()
            .id(option.getId())
            .name(option.getName())
            .category(option.getCategory().name())
            .description(option.getDescription())
            .monthlyFee(option.getMonthlyFee())
            .oneTimeFee(option.getOneTimeFee())
            .isActive(option.getIsActive())
            .effectiveStartDate(option.getEffectiveStartDate())
            .effectiveEndDate(option.getEffectiveEndDate())
            .build();
    }
}
