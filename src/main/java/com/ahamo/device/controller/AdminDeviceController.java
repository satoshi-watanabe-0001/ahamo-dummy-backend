package com.ahamo.device.controller;

import com.ahamo.device.dto.AdminDeviceRequest;
import com.ahamo.device.dto.AdminDeviceResponse;
import com.ahamo.device.dto.DeviceImportResult;
import com.ahamo.device.service.DeviceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/devices")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminDeviceController {
    
    private final DeviceService deviceService;
    
    @PostMapping
    public ResponseEntity<AdminDeviceResponse> createDevice(@Valid @RequestBody AdminDeviceRequest request) {
        AdminDeviceResponse response = deviceService.createDevice(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PutMapping("/{deviceId}")
    public ResponseEntity<AdminDeviceResponse> updateDevice(
            @PathVariable String deviceId,
            @Valid @RequestBody AdminDeviceRequest request) {
        AdminDeviceResponse response = deviceService.updateDevice(deviceId, request);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{deviceId}")
    public ResponseEntity<Map<String, String>> deleteDevice(@PathVariable String deviceId) {
        deviceService.deleteDevice(deviceId);
        return ResponseEntity.ok(Map.of("message", "Device deleted successfully"));
    }
    
    @GetMapping
    public ResponseEntity<List<AdminDeviceResponse>> getAllDevices() {
        List<AdminDeviceResponse> devices = deviceService.getAllDevices();
        return ResponseEntity.ok(devices);
    }
    
    @PostMapping("/import")
    public ResponseEntity<DeviceImportResult> importDevices(@RequestParam("file") MultipartFile file) {
        DeviceImportResult result = deviceService.importDevicesFromCsv(file);
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/export")
    public ResponseEntity<byte[]> exportDevices() {
        byte[] csvData = deviceService.exportDevicesToCsv();
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "devices.csv");
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(csvData);
    }
    
    @PostMapping("/upload-image")
    public ResponseEntity<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file) {
        String imageUrl = deviceService.uploadImage(file);
        return ResponseEntity.ok(Map.of("imageUrl", imageUrl));
    }
}
