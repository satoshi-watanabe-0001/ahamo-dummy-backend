package com.ahamo.device.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "inventory")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Inventory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "device_id", nullable = false)
    private String deviceId;
    
    @Column(nullable = false, length = 50)
    private String color;
    
    @Column(nullable = false, length = 50)
    private String storage;
    
    @Column(name = "total_stock", nullable = false)
    private Integer totalStock = 0;
    
    @Column(name = "available_stock", nullable = false)
    private Integer availableStock = 0;
    
    @Column(name = "reserved_stock", nullable = false)
    private Integer reservedStock = 0;
    
    @Column(name = "allocated_stock", nullable = false)
    private Integer allocatedStock = 0;
    
    @Column(name = "alert_threshold", nullable = false)
    private Integer alertThreshold = 5;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id", insertable = false, updatable = false)
    private Device device;
    
    @PrePersist
    @PreUpdate
    public void updateTimestamp() {
        this.updatedAt = LocalDateTime.now();
    }
}
