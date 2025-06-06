package com.ahamo.plan.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlanResponse {
    private String id;
    private String name;
    private String description;
    private BigDecimal monthlyFee;
    private String dataCapacity;
    private String voiceCalls;
    private String sms;
    private List<String> features;
    private Boolean isActive;
    private Boolean isPopular;
}
