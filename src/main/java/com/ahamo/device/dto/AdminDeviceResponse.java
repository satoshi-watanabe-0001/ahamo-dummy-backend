package com.ahamo.device.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminDeviceResponse {
    
    private String id;
    private String name;
    private String brand;
    private String category;
    private String priceRange;
    private BigDecimal price;
    private List<String> colors;
    private List<String> storageOptions;
    private Boolean inStock;
    private String imageUrl;
    private String specifications;
    private List<String> galleryImages;
    
    private String createdBy;
    private String updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
