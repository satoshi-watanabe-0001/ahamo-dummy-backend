package com.ahamo.device.repository;

import com.ahamo.device.model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    
    Optional<Inventory> findByDeviceIdAndColorAndStorage(String deviceId, String color, String storage);
    
    List<Inventory> findByDeviceId(String deviceId);
    
    @Query("SELECT i FROM Inventory i WHERE i.availableStock <= i.alertThreshold")
    List<Inventory> findLowStockItems();
    
    @Query("SELECT i FROM Inventory i WHERE i.deviceId = :deviceId AND i.availableStock > 0")
    List<Inventory> findAvailableByDeviceId(@Param("deviceId") String deviceId);
    
    @Query("SELECT DISTINCT i.color FROM Inventory i WHERE i.deviceId = :deviceId AND i.availableStock > 0")
    List<String> findAvailableColorsByDeviceId(@Param("deviceId") String deviceId);
    
    @Query("SELECT DISTINCT i.storage FROM Inventory i WHERE i.deviceId = :deviceId AND i.color = :color AND i.availableStock > 0")
    List<String> findAvailableStorageByDeviceIdAndColor(@Param("deviceId") String deviceId, @Param("color") String color);
}
