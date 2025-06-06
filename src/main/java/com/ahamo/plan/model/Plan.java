package com.ahamo.plan.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "plans")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Plan {
    
    @Id
    private String id;
    
    @Column(nullable = false)
    private String name;
    
    private String description;
    
    @Column(name = "monthly_fee", precision = 10, scale = 2)
    private BigDecimal monthlyFee;
    
    @Column(name = "data_capacity")
    private String dataCapacity;
    
    @Column(name = "voice_calls")
    private String voiceCalls;
    
    private String sms;
    
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "plan_features", joinColumns = @JoinColumn(name = "plan_id"))
    @Column(name = "feature")
    private List<String> features;
    
    private String version;
    
    @Column(name = "parent_plan_id")
    private String parentPlanId;
    
    @Column(name = "is_current_version")
    private Boolean isCurrentVersion;
    
    @Column(name = "effective_start_date")
    private LocalDateTime effectiveStartDate;
    
    @Column(name = "effective_end_date")
    private LocalDateTime effectiveEndDate;
    
    @Column(name = "campaign_start_date")
    private LocalDateTime campaignStartDate;
    
    @Column(name = "campaign_end_date")
    private LocalDateTime campaignEndDate;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "created_by")
    private String createdBy;
    
    @Column(name = "updated_by")
    private String updatedBy;
    
    @Column(name = "change_reason")
    private String changeReason;
    
    @Column(name = "approval_status")
    private String approvalStatus;
}
