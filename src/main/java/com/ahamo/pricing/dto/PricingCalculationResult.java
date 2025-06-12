package com.ahamo.pricing.dto;

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
public class PricingCalculationResult {
    
    private BigDecimal monthlyTotal;
    private BigDecimal initialCost;
    private BigDecimal totalAmount;
    private BigDecimal taxIncluded;
    private PricingBreakdown breakdown;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PricingBreakdown {
        private BigDecimal baseFee;
        private BigDecimal callFee;
        private BigDecimal dataFee;
        private DeviceCost deviceCost;
        private List<OptionFee> optionFees;
        private List<Discount> discounts;
        private List<Campaign> campaigns;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DeviceCost {
        private String deviceId;
        private String deviceName;
        private BigDecimal totalPrice;
        private BigDecimal monthlyPayment;
        private String paymentOption;
        private BigDecimal tradeInDiscount;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OptionFee {
        private String id;
        private String name;
        private BigDecimal monthlyFee;
        private BigDecimal oneTimeFee;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Discount {
        private String id;
        private String name;
        private BigDecimal amount;
        private String type;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Campaign {
        private String id;
        private String name;
        private BigDecimal discountAmount;
        private String validUntil;
    }
}
