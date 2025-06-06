package com.ahamo.device.service;

import com.ahamo.common.exception.ErrorCode;
import com.ahamo.common.lock.DistributedLockService;
import com.ahamo.device.dto.InventoryAlertResponse;
import com.ahamo.device.dto.InventoryStatusResponse;
import com.ahamo.device.model.Device;
import com.ahamo.device.model.Inventory;
import com.ahamo.device.repository.DeviceRepository;
import com.ahamo.device.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryServiceImpl implements InventoryService {
    
    private final InventoryRepository inventoryRepository;
    private final DeviceRepository deviceRepository;
    private final DistributedLockService lockService;
    
    @Override
    @Cacheable(value = "inventoryCache", key = "#deviceId")
    public InventoryStatusResponse getInventoryStatus(String deviceId) {
        List<Inventory> inventories = inventoryRepository.findByDeviceId(deviceId);
        
        if (inventories.isEmpty()) {
            return new InventoryStatusResponse(deviceId, new ArrayList<>());
        }
        
        Map<String, List<Inventory>> colorGroups = inventories.stream()
                .collect(Collectors.groupingBy(Inventory::getColor));
        
        List<InventoryStatusResponse.ColorInventory> colorInventories = colorGroups.entrySet().stream()
                .map(entry -> {
                    String color = entry.getKey();
                    List<Inventory> colorInventoryList = entry.getValue();
                    
                    List<InventoryStatusResponse.StorageInventory> storageInventories = colorInventoryList.stream()
                            .map(inv -> new InventoryStatusResponse.StorageInventory(
                                    inv.getStorage(),
                                    inv.getAvailableStock() > 0,
                                    calculateEstimatedDelivery(inv.getAvailableStock()),
                                    inv.getAvailableStock()
                            ))
                            .collect(Collectors.toList());
                    
                    return new InventoryStatusResponse.ColorInventory(color, storageInventories);
                })
                .collect(Collectors.toList());
        
        return new InventoryStatusResponse(deviceId, colorInventories);
    }
    
    @Override
    @Cacheable(value = "allInventoryCache")
    public List<InventoryStatusResponse> getAllInventoryStatus() {
        List<String> deviceIds = deviceRepository.findAll().stream()
                .map(Device::getId)
                .collect(Collectors.toList());
        
        return deviceIds.stream()
                .map(this::getInventoryStatus)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<InventoryAlertResponse> getInventoryAlerts() {
        List<Inventory> lowStockItems = inventoryRepository.findLowStockItems();
        
        return lowStockItems.stream()
                .map(inventory -> {
                    Device device = deviceRepository.findById(inventory.getDeviceId()).orElse(null);
                    String deviceName = device != null ? device.getName() : "Unknown Device";
                    
                    String severity = inventory.getAvailableStock() == 0 ? "CRITICAL" : "WARNING";
                    
                    return new InventoryAlertResponse(
                            inventory.getDeviceId(),
                            deviceName,
                            inventory.getColor(),
                            inventory.getStorage(),
                            inventory.getAvailableStock(),
                            inventory.getAlertThreshold(),
                            inventory.getUpdatedAt(),
                            severity
                    );
                })
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public Inventory getOrCreateInventory(String deviceId, String color, String storage) {
        return inventoryRepository.findByDeviceIdAndColorAndStorage(deviceId, color, storage)
                .orElseGet(() -> {
                    Inventory newInventory = new Inventory();
                    newInventory.setDeviceId(deviceId);
                    newInventory.setColor(color);
                    newInventory.setStorage(storage);
                    newInventory.setTotalStock(0);
                    newInventory.setAvailableStock(0);
                    newInventory.setReservedStock(0);
                    newInventory.setAllocatedStock(0);
                    newInventory.setAlertThreshold(5);
                    return inventoryRepository.save(newInventory);
                });
    }
    
    @Override
    @Transactional
    @CacheEvict(value = {"inventoryCache", "allInventoryCache"}, allEntries = true)
    public void updateInventoryStock(String deviceId, String color, String storage, int totalStock) {
        String lockKey = DistributedLockService.getInventoryLockKey(deviceId, color);
        
        lockService.executeWithLock(lockKey, () -> {
            Inventory inventory = getOrCreateInventory(deviceId, color, storage);
            
            int stockDifference = totalStock - inventory.getTotalStock();
            inventory.setTotalStock(totalStock);
            inventory.setAvailableStock(Math.max(0, inventory.getAvailableStock() + stockDifference));
            
            inventoryRepository.save(inventory);
            log.info("Updated inventory for {}:{}:{} - Total: {}, Available: {}", 
                    deviceId, color, storage, totalStock, inventory.getAvailableStock());
        });
    }
    
    @Override
    public boolean checkAvailability(String deviceId, String color, String storage, int quantity) {
        Inventory inventory = inventoryRepository.findByDeviceIdAndColorAndStorage(deviceId, color, storage)
                .orElse(null);
        
        return inventory != null && inventory.getAvailableStock() >= quantity;
    }
    
    @Override
    @Transactional
    @CacheEvict(value = {"inventoryCache", "allInventoryCache"}, allEntries = true)
    public void reserveStock(Long inventoryId, int quantity) {
        Inventory inventory = inventoryRepository.findById(inventoryId)
                .orElseThrow(() -> new RuntimeException("Inventory not found"));
        
        String lockKey = DistributedLockService.getInventoryLockKey(inventory.getDeviceId(), inventory.getColor());
        
        lockService.executeWithLock(lockKey, () -> {
            Inventory currentInventory = inventoryRepository.findById(inventoryId)
                    .orElseThrow(() -> new RuntimeException("Inventory not found"));
            
            if (currentInventory.getAvailableStock() < quantity) {
                throw new RuntimeException(ErrorCode.INVENTORY_NOT_AVAILABLE.getDefaultMessage());
            }
            
            currentInventory.setAvailableStock(currentInventory.getAvailableStock() - quantity);
            currentInventory.setReservedStock(currentInventory.getReservedStock() + quantity);
            
            inventoryRepository.save(currentInventory);
            log.info("Reserved {} units for inventory ID: {}", quantity, inventoryId);
        });
    }
    
    @Override
    @Transactional
    @CacheEvict(value = {"inventoryCache", "allInventoryCache"}, allEntries = true)
    public void releaseReservedStock(Long inventoryId, int quantity) {
        Inventory inventory = inventoryRepository.findById(inventoryId)
                .orElseThrow(() -> new RuntimeException("Inventory not found"));
        
        String lockKey = DistributedLockService.getInventoryLockKey(inventory.getDeviceId(), inventory.getColor());
        
        lockService.executeWithLock(lockKey, () -> {
            Inventory currentInventory = inventoryRepository.findById(inventoryId)
                    .orElseThrow(() -> new RuntimeException("Inventory not found"));
            
            currentInventory.setAvailableStock(currentInventory.getAvailableStock() + quantity);
            currentInventory.setReservedStock(Math.max(0, currentInventory.getReservedStock() - quantity));
            
            inventoryRepository.save(currentInventory);
            log.info("Released {} reserved units for inventory ID: {}", quantity, inventoryId);
        });
    }
    
    @Override
    @Transactional
    @CacheEvict(value = {"inventoryCache", "allInventoryCache"}, allEntries = true)
    public void allocateStock(Long inventoryId, int quantity) {
        Inventory inventory = inventoryRepository.findById(inventoryId)
                .orElseThrow(() -> new RuntimeException("Inventory not found"));
        
        String lockKey = DistributedLockService.getInventoryLockKey(inventory.getDeviceId(), inventory.getColor());
        
        lockService.executeWithLock(lockKey, () -> {
            Inventory currentInventory = inventoryRepository.findById(inventoryId)
                    .orElseThrow(() -> new RuntimeException("Inventory not found"));
            
            if (currentInventory.getReservedStock() < quantity) {
                throw new RuntimeException(ErrorCode.INSUFFICIENT_STOCK.getDefaultMessage());
            }
            
            currentInventory.setReservedStock(currentInventory.getReservedStock() - quantity);
            currentInventory.setAllocatedStock(currentInventory.getAllocatedStock() + quantity);
            
            inventoryRepository.save(currentInventory);
            log.info("Allocated {} units for inventory ID: {}", quantity, inventoryId);
        });
    }
    
    @Override
    @Transactional
    @CacheEvict(value = {"inventoryCache", "allInventoryCache"}, allEntries = true)
    public void restoreStock(Long inventoryId, int quantity) {
        Inventory inventory = inventoryRepository.findById(inventoryId)
                .orElseThrow(() -> new RuntimeException("Inventory not found"));
        
        String lockKey = DistributedLockService.getInventoryLockKey(inventory.getDeviceId(), inventory.getColor());
        
        lockService.executeWithLock(lockKey, () -> {
            Inventory currentInventory = inventoryRepository.findById(inventoryId)
                    .orElseThrow(() -> new RuntimeException("Inventory not found"));
            
            currentInventory.setAvailableStock(currentInventory.getAvailableStock() + quantity);
            currentInventory.setAllocatedStock(Math.max(0, currentInventory.getAllocatedStock() - quantity));
            
            inventoryRepository.save(currentInventory);
            log.info("Restored {} units to available stock for inventory ID: {}", quantity, inventoryId);
        });
    }
    
    private String calculateEstimatedDelivery(int availableStock) {
        if (availableStock > 0) {
            return LocalDate.now().plusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE);
        } else {
            return LocalDate.now().plusDays(7).format(DateTimeFormatter.ISO_LOCAL_DATE);
        }
    }
}
