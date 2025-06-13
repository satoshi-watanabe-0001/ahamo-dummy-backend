package com.ahamo.monitoring.service;

import com.ahamo.monitoring.dto.AlertRequest;
import com.ahamo.monitoring.dto.AlertResponse;
import com.ahamo.monitoring.model.Alert;
import com.ahamo.monitoring.repository.AlertRepository;
import com.ahamo.device.service.InventoryService;
import com.ahamo.device.dto.InventoryAlertResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlertServiceImpl implements AlertService {
    
    private final AlertRepository alertRepository;
    private final InventoryService inventoryService;
    private final MetricsService metricsService;
    
    @Override
    @Transactional
    public AlertResponse createAlert(AlertRequest alertRequest) {
        log.info("新しいアラートを作成: type={}, severity={}", 
                alertRequest.getAlertType(), alertRequest.getSeverity());
        
        Alert alert = Alert.builder()
                .alertType(alertRequest.getAlertType())
                .severity(alertRequest.getSeverity())
                .title(alertRequest.getTitle())
                .description(alertRequest.getDescription())
                .source(alertRequest.getSource())
                .status("ACTIVE")
                .createdAt(LocalDateTime.now())
                .build();
        
        Alert savedAlert = alertRepository.save(alert);
        
        AlertResponse response = convertToResponse(savedAlert);
        
        sendNotification(response);
        
        return response;
    }
    
    @Override
    public List<AlertResponse> getAllAlerts() {
        log.debug("全アラート取得");
        return alertRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<AlertResponse> getActiveAlerts() {
        log.debug("アクティブアラート取得");
        return alertRepository.findByStatus("ACTIVE").stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public AlertResponse acknowledgeAlert(Long alertId) {
        log.info("アラート確認: alertId={}", alertId);
        
        Alert alert = alertRepository.findById(alertId)
                .orElseThrow(() -> new RuntimeException("アラートが見つかりません: " + alertId));
        
        alert.setStatus("ACKNOWLEDGED");
        alert.setAcknowledgedAt(LocalDateTime.now());
        
        Alert savedAlert = alertRepository.save(alert);
        
        return convertToResponse(savedAlert);
    }
    
    @Override
    @Scheduled(fixedRate = 60000)
    public void processAutomaticAlerts() {
        log.debug("自動アラート処理開始");
        
        try {
            processInventoryAlerts();
            processSystemAlerts();
        } catch (Exception e) {
            log.error("自動アラート処理中にエラーが発生", e);
        }
    }
    
    @Override
    public void sendNotification(AlertResponse alert) {
        log.info("アラート通知送信: alertId={}, severity={}", alert.getId(), alert.getSeverity());
        
        switch (alert.getSeverity()) {
            case "CRITICAL":
                sendCriticalNotification(alert);
                break;
            case "WARNING":
                sendWarningNotification(alert);
                break;
            case "INFO":
                sendInfoNotification(alert);
                break;
            default:
                log.warn("不明なアラート重要度: {}", alert.getSeverity());
        }
    }
    
    private void processInventoryAlerts() {
        List<InventoryAlertResponse> inventoryAlerts = inventoryService.getInventoryAlerts();
        
        for (InventoryAlertResponse inventoryAlert : inventoryAlerts) {
            if (!alertRepository.existsBySourceAndAlertTypeAndStatus(
                    "INVENTORY_" + inventoryAlert.getDeviceId(), "INVENTORY_LOW_STOCK", "ACTIVE")) {
                
                AlertRequest alertRequest = AlertRequest.builder()
                        .alertType("INVENTORY_LOW_STOCK")
                        .severity(inventoryAlert.getSeverity())
                        .title("在庫不足アラート")
                        .description(String.format("デバイス %s (%s/%s) の在庫が不足しています。現在の在庫: %d",
                                inventoryAlert.getDeviceName(),
                                inventoryAlert.getColor(),
                                inventoryAlert.getStorage(),
                                inventoryAlert.getCurrentStock()))
                        .source("INVENTORY_" + inventoryAlert.getDeviceId())
                        .build();
                
                createAlert(alertRequest);
            }
        }
    }
    
    private void processSystemAlerts() {
        try {
            var healthMetrics = metricsService.getHealthMetrics();
            
            if ("UNHEALTHY".equals(healthMetrics.getStatus())) {
                if (!alertRepository.existsBySourceAndAlertTypeAndStatus("SYSTEM", "SYSTEM_HEALTH", "ACTIVE")) {
                    AlertRequest alertRequest = AlertRequest.builder()
                            .alertType("SYSTEM_HEALTH")
                            .severity("CRITICAL")
                            .title("システムヘルス異常")
                            .description("システムヘルスチェックで異常が検出されました")
                            .source("SYSTEM")
                            .build();
                    
                    createAlert(alertRequest);
                }
            }
        } catch (Exception e) {
            log.error("システムアラート処理中にエラーが発生", e);
        }
    }
    
    private void sendCriticalNotification(AlertResponse alert) {
        log.error("【緊急】クリティカルアラート: {}", alert.getTitle());
    }
    
    private void sendWarningNotification(AlertResponse alert) {
        log.warn("【警告】ワーニングアラート: {}", alert.getTitle());
    }
    
    private void sendInfoNotification(AlertResponse alert) {
        log.info("【情報】インフォアラート: {}", alert.getTitle());
    }
    
    private AlertResponse convertToResponse(Alert alert) {
        return AlertResponse.builder()
                .id(alert.getId())
                .alertType(alert.getAlertType())
                .severity(alert.getSeverity())
                .title(alert.getTitle())
                .description(alert.getDescription())
                .source(alert.getSource())
                .status(alert.getStatus())
                .createdAt(alert.getCreatedAt())
                .acknowledgedAt(alert.getAcknowledgedAt())
                .build();
    }
}
