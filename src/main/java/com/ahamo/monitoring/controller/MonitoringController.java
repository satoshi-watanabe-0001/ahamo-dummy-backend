package com.ahamo.monitoring.controller;

import com.ahamo.monitoring.service.MetricsService;
import com.ahamo.monitoring.service.AlertService;
import com.ahamo.monitoring.dto.SystemMetricsResponse;
import com.ahamo.monitoring.dto.AlertRequest;
import com.ahamo.monitoring.dto.AlertResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/monitoring")
@RequiredArgsConstructor
@Slf4j
public class MonitoringController {
    
    private final MetricsService metricsService;
    private final AlertService alertService;
    
    @GetMapping("/metrics")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SystemMetricsResponse> getSystemMetrics() {
        log.info("システムメトリクス取得要求を受信");
        SystemMetricsResponse metrics = metricsService.getSystemMetrics();
        return ResponseEntity.ok(metrics);
    }
    
    @GetMapping("/metrics/application")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SystemMetricsResponse> getApplicationMetrics() {
        log.info("アプリケーションメトリクス取得要求を受信");
        SystemMetricsResponse metrics = metricsService.getApplicationMetrics();
        return ResponseEntity.ok(metrics);
    }
    
    @GetMapping("/metrics/business")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SystemMetricsResponse> getBusinessMetrics() {
        log.info("ビジネスKPIメトリクス取得要求を受信");
        SystemMetricsResponse metrics = metricsService.getBusinessMetrics();
        return ResponseEntity.ok(metrics);
    }
    
    @PostMapping("/alert")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AlertResponse> createAlert(@Valid @RequestBody AlertRequest alertRequest) {
        log.info("アラート作成要求を受信: type={}, severity={}", 
                alertRequest.getAlertType(), alertRequest.getSeverity());
        AlertResponse alert = alertService.createAlert(alertRequest);
        return ResponseEntity.ok(alert);
    }
    
    @GetMapping("/alerts")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AlertResponse>> getAllAlerts() {
        log.info("全アラート取得要求を受信");
        List<AlertResponse> alerts = alertService.getAllAlerts();
        return ResponseEntity.ok(alerts);
    }
    
    @GetMapping("/alerts/active")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AlertResponse>> getActiveAlerts() {
        log.info("アクティブアラート取得要求を受信");
        List<AlertResponse> alerts = alertService.getActiveAlerts();
        return ResponseEntity.ok(alerts);
    }
    
    @PutMapping("/alerts/{alertId}/acknowledge")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AlertResponse> acknowledgeAlert(@PathVariable Long alertId) {
        log.info("アラート確認要求を受信: alertId={}", alertId);
        AlertResponse alert = alertService.acknowledgeAlert(alertId);
        return ResponseEntity.ok(alert);
    }
    
    @GetMapping("/health")
    public ResponseEntity<SystemMetricsResponse> getHealthCheck() {
        log.debug("ヘルスチェック要求を受信");
        SystemMetricsResponse health = metricsService.getHealthMetrics();
        return ResponseEntity.ok(health);
    }
}
