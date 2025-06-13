package com.ahamo.contract.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlanChangeRequest {
    private String newPlanId;
    private String reason;
    private LocalDateTime effectiveDate;
}
