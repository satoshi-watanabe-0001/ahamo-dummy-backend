package com.ahamo.monitoring.service;

import com.ahamo.monitoring.dto.SystemMetricsResponse;

public interface MetricsService {
    
    SystemMetricsResponse getSystemMetrics();
    
    SystemMetricsResponse getApplicationMetrics();
    
    SystemMetricsResponse getBusinessMetrics();
    
    SystemMetricsResponse getHealthMetrics();
    
    void recordCustomMetric(String metricName, double value);
    
    void recordCustomMetric(String metricName, double value, String... tags);
}
