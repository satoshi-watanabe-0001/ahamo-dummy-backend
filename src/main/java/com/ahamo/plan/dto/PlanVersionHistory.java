package com.ahamo.plan.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlanVersionHistory {
    
    private String version;
    private String changeReason;
    private String createdBy;
    private LocalDateTime createdAt;
    private Boolean isCurrentVersion;
}
