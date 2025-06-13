package com.ahamo.contract.dto;

import com.ahamo.contract.model.ContractChangeHistory;
import lombok.Data;
import lombok.Builder;

import java.time.LocalDateTime;

@Data
@Builder
public class ContractChangeHistoryResponse {
    private Long id;
    private String contractUuid;
    private String changeType;
    private String oldValue;
    private String newValue;
    private String reason;
    private LocalDateTime effectiveDate;
    private LocalDateTime createdAt;
    private String createdBy;
    
    public static ContractChangeHistoryResponse fromHistory(ContractChangeHistory history) {
        return ContractChangeHistoryResponse.builder()
            .id(history.getId())
            .contractUuid(history.getContractUuid())
            .changeType(history.getChangeType().name())
            .oldValue(history.getOldValue())
            .newValue(history.getNewValue())
            .reason(history.getReason())
            .effectiveDate(history.getEffectiveDate())
            .createdAt(history.getCreatedAt())
            .createdBy(history.getCreatedBy())
            .build();
    }
}
