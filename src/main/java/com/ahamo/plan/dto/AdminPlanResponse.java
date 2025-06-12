package com.ahamo.plan.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminPlanResponse {
    
    private String id;
    private String name;
    private String description;
    private BigDecimal monthlyFee;
    private String dataCapacity;
    private String voiceCalls;
    private String sms;
    private java.util.List<String> features;
    
    private String version;
    private String parentPlanId;
    private Boolean isCurrentVersion;
    
    private LocalDateTime effectiveStartDate;
    private LocalDateTime effectiveEndDate;
    
    private LocalDateTime campaignStartDate;
    private LocalDateTime campaignEndDate;
    
    private String createdBy;
    private String updatedBy;
    private String changeReason;
    private String approvalStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
