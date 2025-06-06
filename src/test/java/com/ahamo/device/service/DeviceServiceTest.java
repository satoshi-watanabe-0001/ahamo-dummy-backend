package com.ahamo.device.service;

import com.ahamo.device.dto.AdminDeviceRequest;
import com.ahamo.device.dto.AdminDeviceResponse;
import com.ahamo.device.model.Device;
import com.ahamo.device.repository.DeviceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeviceServiceTest {

    @Mock
    private DeviceRepository deviceRepository;

    @InjectMocks
    private DeviceServiceImpl deviceService;

    private AdminDeviceRequest adminDeviceRequest;
    private Device device;

    @BeforeEach
    void setUp() {
        adminDeviceRequest = new AdminDeviceRequest();
        adminDeviceRequest.setName("iPhone 15");
        adminDeviceRequest.setBrand("Apple");
        adminDeviceRequest.setCategory("iPhone");
        adminDeviceRequest.setPriceRange("premium");
        adminDeviceRequest.setPrice(new BigDecimal("124800.00"));
        adminDeviceRequest.setColors(Arrays.asList("ブラック", "ブルー"));
        adminDeviceRequest.setStorageOptions(Arrays.asList("128GB", "256GB"));
        adminDeviceRequest.setInStock(true);

        device = new Device();
        device.setId("device_iphone15_001");
        device.setName("iPhone 15");
        device.setBrand("Apple");
        device.setCategory(Device.DeviceCategory.iPhone);
        device.setPriceRange(Device.PriceRange.premium);
        device.setPrice(new BigDecimal("124800.00"));
        device.setColors("ブラック,ブルー");
        device.setStorageOptions("128GB,256GB");
        device.setInStock(true);
        device.setCreatedAt(LocalDateTime.now());
        device.setUpdatedAt(LocalDateTime.now());
        device.setCreatedBy("admin@example.com");
        device.setUpdatedBy("admin@example.com");
    }

    @Test
    void createDevice_ValidRequest_ReturnsAdminDeviceResponse() {
        when(deviceRepository.save(any(Device.class))).thenReturn(device);

        AdminDeviceResponse result = deviceService.createDevice(adminDeviceRequest);

        assertNotNull(result);
        assertEquals("iPhone 15", result.getName());
        assertEquals("Apple", result.getBrand());
        assertEquals("iPhone", result.getCategory());
        assertEquals("premium", result.getPriceRange());
        assertEquals(new BigDecimal("124800.00"), result.getPrice());
        assertTrue(result.getInStock());

        verify(deviceRepository, times(1)).save(any(Device.class));
    }

    @Test
    void updateDevice_ExistingDevice_ReturnsUpdatedDevice() {
        when(deviceRepository.findById(anyString())).thenReturn(Optional.of(device));
        when(deviceRepository.save(any(Device.class))).thenReturn(device);

        adminDeviceRequest.setName("iPhone 15 Updated");
        device.setName("iPhone 15 Updated");

        AdminDeviceResponse result = deviceService.updateDevice("device_iphone15_001", adminDeviceRequest);

        assertNotNull(result);
        assertEquals("iPhone 15 Updated", result.getName());

        verify(deviceRepository, times(1)).findById("device_iphone15_001");
        verify(deviceRepository, times(1)).save(any(Device.class));
    }

    @Test
    void updateDevice_NonExistentDevice_ThrowsException() {
        when(deviceRepository.findById(anyString())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            deviceService.updateDevice("nonexistent", adminDeviceRequest);
        });

        verify(deviceRepository, times(1)).findById("nonexistent");
        verify(deviceRepository, never()).save(any(Device.class));
    }

    @Test
    void deleteDevice_ExistingDevice_DeletesDevice() {
        when(deviceRepository.findById(anyString())).thenReturn(Optional.of(device));

        deviceService.deleteDevice("device_iphone15_001");

        verify(deviceRepository, times(1)).findById("device_iphone15_001");
        verify(deviceRepository, times(1)).delete(device);
    }

    @Test
    void deleteDevice_NonExistentDevice_ThrowsException() {
        when(deviceRepository.findById(anyString())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            deviceService.deleteDevice("nonexistent");
        });

        verify(deviceRepository, times(1)).findById("nonexistent");
        verify(deviceRepository, never()).delete(any(Device.class));
    }

    @Test
    void getAllDevices_ReturnsDeviceList() {
        List<Device> devices = Arrays.asList(device);
        when(deviceRepository.findAll()).thenReturn(devices);

        List<AdminDeviceResponse> result = deviceService.getAllDevices();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("iPhone 15", result.get(0).getName());

        verify(deviceRepository, times(1)).findAll();
    }

    @Test
    void updateInventoryStatus_UpdatesAllDevices() {
        List<Device> devices = Arrays.asList(device);
        when(deviceRepository.findAll()).thenReturn(devices);

        deviceService.updateInventoryStatus();

        verify(deviceRepository, times(1)).findAll();
        verify(deviceRepository, times(1)).saveAll(devices);
    }
}
