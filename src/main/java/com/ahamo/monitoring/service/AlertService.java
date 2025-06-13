package com.ahamo.monitoring.service;

import com.ahamo.monitoring.dto.AlertRequest;
import com.ahamo.monitoring.dto.AlertResponse;

import java.util.List;

public interface AlertService {
    
    AlertResponse createAlert(AlertRequest alertRequest);
    
    List<AlertResponse> getAllAlerts();
    
    List<AlertResponse> getActiveAlerts();
    
    AlertResponse acknowledgeAlert(Long alertId);
    
    void processAutomaticAlerts();
    
    void sendNotification(AlertResponse alert);
}
