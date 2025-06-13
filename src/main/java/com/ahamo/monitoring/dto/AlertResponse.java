package com.ahamo.monitoring.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlertResponse {
    
    private Long id;
    private String alertType;
    private String severity;
    private String title;
    private String description;
    private String source;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime acknowledgedAt;
    private LocalDateTime resolvedAt;
    private Map<String, Object> metadata;
}
