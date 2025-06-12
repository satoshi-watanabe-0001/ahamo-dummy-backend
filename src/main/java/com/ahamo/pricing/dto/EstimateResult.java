package com.ahamo.pricing.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EstimateResult {
    
    private String estimateId;
    private LocalDateTime createdAt;
    private LocalDateTime validUntil;
    
    private EstimateSummary summary;
    private List<MonthlyBreakdown> monthlyBreakdowns;
    private EstimateDetails details;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class EstimateSummary {
        private BigDecimal totalInitialCost;
        private BigDecimal averageMonthlyFee;
        private BigDecimal totalAmountForPeriod;
        private BigDecimal totalTaxAmount;
        private Integer estimatePeriodMonths;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MonthlyBreakdown {
        private Integer month;
        private BigDecimal baseFee;
        private BigDecimal devicePayment;
        private BigDecimal optionFees;
        private BigDecimal discounts;
        private BigDecimal subtotal;
        private BigDecimal tax;
        private BigDecimal total;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class EstimateDetails {
        private PlanDetails plan;
        private DeviceDetails device;
        private List<OptionDetails> options;
        private List<DiscountDetails> discounts;
        private List<CampaignDetails> campaigns;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PlanDetails {
        private String id;
        private String name;
        private String description;
        private BigDecimal monthlyFee;
        private String dataCapacity;
        private String voiceCalls;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DeviceDetails {
        private String id;
        private String name;
        private String brand;
        private String color;
        private String storage;
        private BigDecimal price;
        private String paymentOption;
        private BigDecimal monthlyPayment;
        private BigDecimal tradeInValue;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OptionDetails {
        private String id;
        private String name;
        private String category;
        private BigDecimal monthlyFee;
        private BigDecimal oneTimeFee;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DiscountDetails {
        private String id;
        private String name;
        private BigDecimal amount;
        private String type;
        private String description;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CampaignDetails {
        private String id;
        private String name;
        private BigDecimal discountAmount;
        private String validFrom;
        private String validUntil;
        private String description;
    }
}
