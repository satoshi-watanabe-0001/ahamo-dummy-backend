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
public class SystemMetricsResponse {
    
    private LocalDateTime timestamp;
    private String metricType;
    private Map<String, Object> metrics;
    private String status;
    private String message;
}
