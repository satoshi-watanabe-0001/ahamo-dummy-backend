package com.ahamo.device.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryAlertResponse {
    
    private String deviceId;
    private String deviceName;
    private String color;
    private String storage;
    private int currentStock;
    private int alertThreshold;
    private LocalDateTime lastUpdated;
    private String severity;
}
