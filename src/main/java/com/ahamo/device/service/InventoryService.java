package com.ahamo.device.service;

import com.ahamo.device.dto.InventoryStatusResponse;
import com.ahamo.device.dto.InventoryAlertResponse;
import com.ahamo.device.model.Inventory;

import java.util.List;

public interface InventoryService {
    
    InventoryStatusResponse getInventoryStatus(String deviceId);
    
    List<InventoryStatusResponse> getAllInventoryStatus();
    
    List<InventoryAlertResponse> getInventoryAlerts();
    
    Inventory getOrCreateInventory(String deviceId, String color, String storage);
    
    void updateInventoryStock(String deviceId, String color, String storage, int totalStock);
    
    boolean checkAvailability(String deviceId, String color, String storage, int quantity);
    
    void reserveStock(Long inventoryId, int quantity);
    
    void releaseReservedStock(Long inventoryId, int quantity);
    
    void allocateStock(Long inventoryId, int quantity);
    
    void restoreStock(Long inventoryId, int quantity);
}
