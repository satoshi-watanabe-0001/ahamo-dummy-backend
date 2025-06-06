package com.ahamo.device.service;

import com.ahamo.device.dto.AdminDeviceRequest;
import com.ahamo.device.dto.AdminDeviceResponse;
import com.ahamo.device.dto.DeviceImportResult;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public interface DeviceService {
    
    AdminDeviceResponse createDevice(AdminDeviceRequest request);
    
    AdminDeviceResponse updateDevice(String deviceId, AdminDeviceRequest request);
    
    void deleteDevice(String deviceId);
    
    List<AdminDeviceResponse> getAllDevices();
    
    DeviceImportResult importDevicesFromCsv(MultipartFile file);
    
    byte[] exportDevicesToCsv();
    
    String uploadImage(MultipartFile file);
    
    void updateInventoryStatus();
}
