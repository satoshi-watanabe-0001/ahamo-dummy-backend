package com.ahamo.plan.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeeCalculationResult {
    
    private BigDecimal totalFee;
    private FeeBreakdown breakdown;
    private BigDecimal taxIncluded;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class FeeBreakdown {
        private BigDecimal baseFee;
        private BigDecimal callFee;
        private BigDecimal dataFee;
        private List<OptionFee> optionFees;
        private List<Discount> discounts;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OptionFee {
        private String id;
        private String name;
        private BigDecimal fee;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Discount {
        private String id;
        private String name;
        private BigDecimal amount;
    }
}
