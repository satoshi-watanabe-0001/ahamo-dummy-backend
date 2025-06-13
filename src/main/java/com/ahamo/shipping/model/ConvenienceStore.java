package com.ahamo.shipping.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "convenience_stores")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConvenienceStore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "store_code", unique = true)
    private String storeCode;

    @NotBlank
    @Column(name = "store_name")
    private String storeName;

    @NotBlank
    @Column(name = "chain_name")
    private String chainName;

    @NotBlank
    @Column(name = "postal_code")
    private String postalCode;

    @NotBlank
    @Column(name = "prefecture")
    private String prefecture;

    @NotBlank
    @Column(name = "city")
    private String city;

    @NotBlank
    @Column(name = "address_line1")
    private String addressLine1;

    @Column(name = "address_line2")
    private String addressLine2;

    @Column(name = "phone")
    private String phone;

    @Column(name = "latitude", precision = 10, scale = 8)
    private BigDecimal latitude;

    @Column(name = "longitude", precision = 11, scale = 8)
    private BigDecimal longitude;

    @Column(name = "operating_hours")
    private String operatingHours;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
