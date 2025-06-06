package com.ahamo.device.controller;

import com.ahamo.device.dto.AdminDeviceRequest;
import com.ahamo.device.dto.AdminDeviceResponse;
import com.ahamo.device.dto.DeviceImportResult;
import com.ahamo.device.service.DeviceService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AdminDeviceController.class)
class AdminDeviceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DeviceService deviceService;

    @MockBean
    private com.ahamo.security.jwt.JwtTokenProvider jwtTokenProvider;

    @MockBean
    private com.ahamo.security.service.CustomUserDetailsService customUserDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    private AdminDeviceRequest adminDeviceRequest;
    private AdminDeviceResponse adminDeviceResponse;

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
        adminDeviceRequest.setImageUrl("/images/iphone15.jpg");

        adminDeviceResponse = new AdminDeviceResponse();
        adminDeviceResponse.setId("device_iphone15_001");
        adminDeviceResponse.setName("iPhone 15");
        adminDeviceResponse.setBrand("Apple");
        adminDeviceResponse.setCategory("iPhone");
        adminDeviceResponse.setPriceRange("premium");
        adminDeviceResponse.setPrice(new BigDecimal("124800.00"));
        adminDeviceResponse.setColors(Arrays.asList("ブラック", "ブルー"));
        adminDeviceResponse.setStorageOptions(Arrays.asList("128GB", "256GB"));
        adminDeviceResponse.setInStock(true);
        adminDeviceResponse.setImageUrl("/images/iphone15.jpg");
        adminDeviceResponse.setCreatedBy("admin@example.com");
        adminDeviceResponse.setUpdatedBy("admin@example.com");
        adminDeviceResponse.setCreatedAt(LocalDateTime.now());
        adminDeviceResponse.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createDevice_ValidRequest_ReturnsCreatedDevice() throws Exception {
        when(deviceService.createDevice(any(AdminDeviceRequest.class))).thenReturn(adminDeviceResponse);

        mockMvc.perform(post("/api/v1/admin/devices")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(adminDeviceRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("device_iphone15_001"))
                .andExpect(jsonPath("$.name").value("iPhone 15"))
                .andExpect(jsonPath("$.brand").value("Apple"))
                .andExpect(jsonPath("$.category").value("iPhone"))
                .andExpect(jsonPath("$.priceRange").value("premium"))
                .andExpect(jsonPath("$.price").value(124800.00))
                .andExpect(jsonPath("$.inStock").value(true))
                .andExpect(jsonPath("$.createdBy").value("admin@example.com"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateDevice_ValidRequest_ReturnsUpdatedDevice() throws Exception {
        adminDeviceRequest.setName("iPhone 15 Updated");
        adminDeviceResponse.setName("iPhone 15 Updated");

        when(deviceService.updateDevice(anyString(), any(AdminDeviceRequest.class))).thenReturn(adminDeviceResponse);

        mockMvc.perform(put("/api/v1/admin/devices/device_iphone15_001")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(adminDeviceRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("device_iphone15_001"))
                .andExpect(jsonPath("$.name").value("iPhone 15 Updated"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteDevice_ValidDeviceId_ReturnsSuccess() throws Exception {
        doNothing().when(deviceService).deleteDevice(anyString());

        mockMvc.perform(delete("/api/v1/admin/devices/device_iphone15_001")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Device deleted successfully"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllDevices_ReturnsDeviceList() throws Exception {
        List<AdminDeviceResponse> devices = Arrays.asList(adminDeviceResponse);
        when(deviceService.getAllDevices()).thenReturn(devices);

        mockMvc.perform(get("/api/v1/admin/devices")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value("device_iphone15_001"))
                .andExpect(jsonPath("$[0].name").value("iPhone 15"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void importDevices_ValidCsv_ReturnsImportResult() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "devices.csv", "text/csv", "id,name,brand,category,price_range,price\ndevice_001,Test Device,Test Brand,iPhone,premium,100000".getBytes());
        
        DeviceImportResult result = new DeviceImportResult();
        result.setTotalRows(1);
        result.setSuccessfulRows(1);
        result.setFailedRows(0);
        result.setErrors(Arrays.asList());

        when(deviceService.importDevicesFromCsv(any())).thenReturn(result);

        mockMvc.perform(multipart("/api/v1/admin/devices/import")
                .file(file)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalRows").value(1))
                .andExpect(jsonPath("$.successfulRows").value(1))
                .andExpect(jsonPath("$.failedRows").value(0));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void exportDevices_ReturnsCsvFile() throws Exception {
        byte[] csvData = "id,name,brand,category,price_range,price\ndevice_001,Test Device,Test Brand,iPhone,premium,100000".getBytes();
        when(deviceService.exportDevicesToCsv()).thenReturn(csvData);

        mockMvc.perform(get("/api/v1/admin/devices/export")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "form-data; name=\"attachment\"; filename=\"devices.csv\""))
                .andExpect(content().bytes(csvData));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void uploadImage_ValidImage_ReturnsImageUrl() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "test image content".getBytes());
        
        when(deviceService.uploadImage(any())).thenReturn("/uploads/images/test.jpg");

        mockMvc.perform(multipart("/api/v1/admin/devices/upload-image")
                .file(file)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.imageUrl").value("/uploads/images/test.jpg"));
    }

    @Test
    void createDevice_Unauthorized_ReturnsForbidden() throws Exception {
        mockMvc.perform(post("/api/v1/admin/devices")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(adminDeviceRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    void createDevice_InsufficientRole_ReturnsForbidden() throws Exception {
        mockMvc.perform(post("/api/v1/admin/devices")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(adminDeviceRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createDevice_InvalidRequest_ReturnsBadRequest() throws Exception {
        AdminDeviceRequest invalidRequest = new AdminDeviceRequest();

        mockMvc.perform(post("/api/v1/admin/devices")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
}
