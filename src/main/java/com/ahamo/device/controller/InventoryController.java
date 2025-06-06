package com.ahamo.device.controller;

import com.ahamo.device.dto.InventoryAlertResponse;
import com.ahamo.device.dto.InventoryStatusResponse;
import com.ahamo.device.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class InventoryController {
    
    private final InventoryService inventoryService;
    
    @GetMapping("/inventory")
    public ResponseEntity<List<InventoryStatusResponse>> getAllInventoryStatus() {
        List<InventoryStatusResponse> inventoryStatus = inventoryService.getAllInventoryStatus();
        return ResponseEntity.ok(inventoryStatus);
    }
    
    @GetMapping("/inventory/{deviceId}")
    public ResponseEntity<InventoryStatusResponse> getInventoryStatus(@PathVariable String deviceId) {
        InventoryStatusResponse inventoryStatus = inventoryService.getInventoryStatus(deviceId);
        return ResponseEntity.ok(inventoryStatus);
    }
    
    @GetMapping("/inventory/alerts")
    public ResponseEntity<List<InventoryAlertResponse>> getInventoryAlerts() {
        List<InventoryAlertResponse> alerts = inventoryService.getInventoryAlerts();
        return ResponseEntity.ok(alerts);
    }
    
    @PutMapping("/inventory/{deviceId}/{color}/{storage}")
    public ResponseEntity<Void> updateInventoryStock(
            @PathVariable String deviceId,
            @PathVariable String color,
            @PathVariable String storage,
            @RequestParam int totalStock) {
        inventoryService.updateInventoryStock(deviceId, color, storage, totalStock);
        return ResponseEntity.ok().build();
    }
}
