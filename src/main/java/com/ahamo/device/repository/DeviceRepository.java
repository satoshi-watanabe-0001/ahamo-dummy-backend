package com.ahamo.device.repository;

import com.ahamo.device.model.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeviceRepository extends JpaRepository<Device, String> {
    
    List<Device> findByInStockTrue();
    
    List<Device> findByCategory(Device.DeviceCategory category);
    
    List<Device> findByPriceRange(Device.PriceRange priceRange);
    
    List<Device> findByBrand(String brand);
}
