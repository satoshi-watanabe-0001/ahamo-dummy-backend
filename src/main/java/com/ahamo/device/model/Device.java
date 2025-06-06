package com.ahamo.device.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "devices")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Device {
    
    @Id
    private String id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private String brand;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeviceCategory category;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "price_range", nullable = false)
    private PriceRange priceRange;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;
    
    @Column(columnDefinition = "TEXT")
    private String colors;
    
    @Column(name = "storage_options", columnDefinition = "TEXT")
    private String storageOptions;
    
    @Column(name = "in_stock")
    private Boolean inStock;
    
    @Column(name = "image_url")
    private String imageUrl;
    
    @Column(columnDefinition = "TEXT")
    private String specifications;
    
    @Column(name = "gallery_images", columnDefinition = "TEXT")
    private String galleryImages;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "created_by")
    private String createdBy;
    
    @Column(name = "updated_by")
    private String updatedBy;
    
    public enum DeviceCategory {
        iPhone, Android
    }
    
    public enum PriceRange {
        entry, mid, premium
    }
}
