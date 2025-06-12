package com.ahamo.contract.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContractAuditTrailResponse {

    private String contractId;
    private List<AuditEvent> auditEvents;
    private LocalDateTime generatedAt;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AuditEvent {
        private String eventId;
        private String eventType;
        private LocalDateTime timestamp;
        private String userId;
        private String userName;
        private String ipAddress;
        private String userAgent;
        private Map<String, Object> details;
        private Map<String, Object> evidence;
    }
}
