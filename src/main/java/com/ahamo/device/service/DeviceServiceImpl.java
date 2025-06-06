package com.ahamo.device.service;

import com.ahamo.device.dto.AdminDeviceRequest;
import com.ahamo.device.dto.AdminDeviceResponse;
import com.ahamo.device.dto.DeviceImportResult;
import com.ahamo.device.model.Device;
import com.ahamo.device.repository.DeviceRepository;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.imgscalr.Scalr;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeviceServiceImpl implements DeviceService {
    
    private final DeviceRepository deviceRepository;
    private final InventoryService inventoryService;
    
    private static final String UPLOAD_DIR = "uploads/images/";
    private static final int MAX_IMAGE_WIDTH = 1024;
    private static final long MAX_FILE_SIZE = 1024 * 1024; // 1MB
    
    @Override
    public AdminDeviceResponse createDevice(AdminDeviceRequest request) {
        Device device = new Device();
        device.setId("device_" + UUID.randomUUID().toString().replace("-", "").substring(0, 8));
        device.setName(request.getName());
        device.setBrand(request.getBrand());
        device.setCategory(Device.DeviceCategory.valueOf(request.getCategory()));
        device.setPriceRange(Device.PriceRange.valueOf(request.getPriceRange()));
        device.setPrice(request.getPrice());
        device.setColors(request.getColors() != null ? String.join(",", request.getColors()) : null);
        device.setStorageOptions(request.getStorageOptions() != null ? String.join(",", request.getStorageOptions()) : null);
        device.setInStock(request.getInStock() != null ? request.getInStock() : true);
        device.setImageUrl(request.getImageUrl());
        device.setSpecifications(request.getSpecifications());
        device.setGalleryImages(request.getGalleryImages() != null ? String.join(",", request.getGalleryImages()) : null);
        device.setCreatedAt(LocalDateTime.now());
        device.setUpdatedAt(LocalDateTime.now());
        device.setCreatedBy("admin@example.com");
        device.setUpdatedBy("admin@example.com");
        
        Device savedDevice = deviceRepository.save(device);
        return convertToAdminResponse(savedDevice);
    }
    
    @Override
    public AdminDeviceResponse updateDevice(String deviceId, AdminDeviceRequest request) {
        Device existingDevice = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new RuntimeException("Device not found: " + deviceId));
        
        existingDevice.setName(request.getName());
        existingDevice.setBrand(request.getBrand());
        existingDevice.setCategory(Device.DeviceCategory.valueOf(request.getCategory()));
        existingDevice.setPriceRange(Device.PriceRange.valueOf(request.getPriceRange()));
        existingDevice.setPrice(request.getPrice());
        existingDevice.setColors(request.getColors() != null ? String.join(",", request.getColors()) : null);
        existingDevice.setStorageOptions(request.getStorageOptions() != null ? String.join(",", request.getStorageOptions()) : null);
        existingDevice.setInStock(request.getInStock() != null ? request.getInStock() : true);
        existingDevice.setImageUrl(request.getImageUrl());
        existingDevice.setSpecifications(request.getSpecifications());
        existingDevice.setGalleryImages(request.getGalleryImages() != null ? String.join(",", request.getGalleryImages()) : null);
        existingDevice.setUpdatedAt(LocalDateTime.now());
        existingDevice.setUpdatedBy("admin@example.com");
        
        Device savedDevice = deviceRepository.save(existingDevice);
        return convertToAdminResponse(savedDevice);
    }
    
    @Override
    public void deleteDevice(String deviceId) {
        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new RuntimeException("Device not found: " + deviceId));
        
        deviceRepository.delete(device);
    }
    
    @Override
    public List<AdminDeviceResponse> getAllDevices() {
        List<Device> devices = deviceRepository.findAll();
        return devices.stream()
                .map(this::convertToAdminResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public DeviceImportResult importDevicesFromCsv(MultipartFile file) {
        List<DeviceImportResult.DeviceImportError> errors = new ArrayList<>();
        int totalRows = 0;
        int successfulRows = 0;
        
        try (CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
            String[] headers = reader.readNext();
            if (headers == null || headers.length < 5) {
                throw new RuntimeException("Invalid CSV format");
            }
            
            String[] row;
            int rowNumber = 1;
            
            while ((row = reader.readNext()) != null) {
                totalRows++;
                rowNumber++;
                
                try {
                    if (row.length < 5) {
                        errors.add(new DeviceImportResult.DeviceImportError(rowNumber, "", "Insufficient columns"));
                        continue;
                    }
                    
                    AdminDeviceRequest request = new AdminDeviceRequest();
                    request.setName(row[1]);
                    request.setBrand(row[2]);
                    request.setCategory(row[3]);
                    request.setPriceRange(row[4]);
                    request.setPrice(new BigDecimal(row[5]));
                    
                    if (row.length > 6 && !row[6].isEmpty()) {
                        request.setColors(Arrays.asList(row[6].split(";")));
                    }
                    if (row.length > 7 && !row[7].isEmpty()) {
                        request.setStorageOptions(Arrays.asList(row[7].split(";")));
                    }
                    if (row.length > 8 && !row[8].isEmpty()) {
                        request.setInStock(Boolean.parseBoolean(row[8]));
                    }
                    if (row.length > 9 && !row[9].isEmpty()) {
                        request.setImageUrl(row[9]);
                    }
                    
                    createDevice(request);
                    successfulRows++;
                    
                } catch (Exception e) {
                    errors.add(new DeviceImportResult.DeviceImportError(rowNumber, row[0], e.getMessage()));
                }
            }
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to import CSV: " + e.getMessage());
        }
        
        return new DeviceImportResult(totalRows, successfulRows, totalRows - successfulRows, errors);
    }
    
    @Override
    public byte[] exportDevicesToCsv() {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             OutputStreamWriter writer = new OutputStreamWriter(outputStream);
             CSVWriter csvWriter = new CSVWriter(writer)) {
            
            String[] headers = {"id", "name", "brand", "category", "price_range", "price", "colors", "storage_options", "in_stock", "image_url"};
            csvWriter.writeNext(headers);
            
            List<Device> devices = deviceRepository.findAll();
            for (Device device : devices) {
                String[] row = {
                    device.getId(),
                    device.getName(),
                    device.getBrand(),
                    device.getCategory().name(),
                    device.getPriceRange().name(),
                    device.getPrice().toString(),
                    device.getColors() != null ? device.getColors() : "",
                    device.getStorageOptions() != null ? device.getStorageOptions() : "",
                    device.getInStock().toString(),
                    device.getImageUrl() != null ? device.getImageUrl() : ""
                };
                csvWriter.writeNext(row);
            }
            
            csvWriter.flush();
            return outputStream.toByteArray();
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to export CSV: " + e.getMessage());
        }
    }
    
    @Override
    public String uploadImage(MultipartFile file) {
        if (file.isEmpty()) {
            throw new RuntimeException("File is empty");
        }
        
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new RuntimeException("File size exceeds 1MB limit");
        }
        
        String contentType = file.getContentType();
        if (contentType == null || (!contentType.equals("image/jpeg") && 
                                   !contentType.equals("image/png") && 
                                   !contentType.equals("image/webp"))) {
            throw new RuntimeException("Invalid file format. Only JPEG, PNG, and WebP are allowed");
        }
        
        try {
            BufferedImage originalImage = ImageIO.read(file.getInputStream());
            if (originalImage == null) {
                throw new RuntimeException("Invalid image file");
            }
            
            BufferedImage resizedImage = Scalr.resize(originalImage, Scalr.Method.QUALITY, 
                                                     Scalr.Mode.FIT_TO_WIDTH, MAX_IMAGE_WIDTH);
            
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path uploadPath = Paths.get(UPLOAD_DIR);
            Files.createDirectories(uploadPath);
            
            Path filePath = uploadPath.resolve(fileName);
            String formatName = contentType.substring(contentType.lastIndexOf("/") + 1);
            if (formatName.equals("webp")) {
                formatName = "png";
            }
            
            ImageIO.write(resizedImage, formatName, filePath.toFile());
            
            return "/uploads/images/" + fileName;
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload image: " + e.getMessage());
        }
    }
    
    @Override
    public void updateInventoryStatus() {
        List<Device> devices = deviceRepository.findAll();
        Random random = new Random();
        
        for (Device device : devices) {
            device.setInStock(random.nextBoolean());
            device.setUpdatedAt(LocalDateTime.now());
            device.setUpdatedBy("system");
            
            if (device.getColors() != null && device.getStorageOptions() != null) {
                String[] colors = device.getColors().split(",");
                String[] storages = device.getStorageOptions().split(",");
                
                for (String color : colors) {
                    for (String storage : storages) {
                        int randomStock = random.nextInt(20);
                        try {
                            inventoryService.updateInventoryStock(device.getId(), color.trim(), storage.trim(), randomStock);
                        } catch (Exception e) {
                            log.error("Failed to update inventory for {}:{}:{}", device.getId(), color.trim(), storage.trim(), e);
                        }
                    }
                }
            }
        }
        
        deviceRepository.saveAll(devices);
    }
    
    private AdminDeviceResponse convertToAdminResponse(Device device) {
        AdminDeviceResponse response = new AdminDeviceResponse();
        response.setId(device.getId());
        response.setName(device.getName());
        response.setBrand(device.getBrand());
        response.setCategory(device.getCategory().name());
        response.setPriceRange(device.getPriceRange().name());
        response.setPrice(device.getPrice());
        response.setColors(device.getColors() != null ? Arrays.asList(device.getColors().split(",")) : null);
        response.setStorageOptions(device.getStorageOptions() != null ? Arrays.asList(device.getStorageOptions().split(",")) : null);
        response.setInStock(device.getInStock());
        response.setImageUrl(device.getImageUrl());
        response.setSpecifications(device.getSpecifications());
        response.setGalleryImages(device.getGalleryImages() != null ? Arrays.asList(device.getGalleryImages().split(",")) : null);
        response.setCreatedBy(device.getCreatedBy());
        response.setUpdatedBy(device.getUpdatedBy());
        response.setCreatedAt(device.getCreatedAt());
        response.setUpdatedAt(device.getUpdatedAt());
        return response;
    }
}
