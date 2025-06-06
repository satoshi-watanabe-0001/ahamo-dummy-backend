package com.ahamo.device.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryStatusResponse {
    
    private String deviceId;
    private List<ColorInventory> availableColors;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ColorInventory {
        private String color;
        private List<StorageInventory> storageOptions;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StorageInventory {
        private String storage;
        private boolean inStock;
        private String estimatedDelivery;
        private int availableQuantity;
    }
}
