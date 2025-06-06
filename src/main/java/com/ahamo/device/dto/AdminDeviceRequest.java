package com.ahamo.device.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.DecimalMin;
import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminDeviceRequest {
    
    @NotBlank(message = "Device name is required")
    private String name;
    
    @NotBlank(message = "Brand is required")
    private String brand;
    
    @NotBlank(message = "Category is required")
    private String category;
    
    @NotBlank(message = "Price range is required")
    private String priceRange;
    
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", message = "Price must be positive")
    private BigDecimal price;
    
    private List<String> colors;
    
    private List<String> storageOptions;
    
    private Boolean inStock;
    
    private String imageUrl;
    
    private String specifications;
    
    private List<String> galleryImages;
}
