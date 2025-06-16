package com.ahamo.shipping.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "shipping_orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShippingOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "order_number", unique = true)
    private String orderNumber;

    @NotNull
    @Column(name = "contract_id")
    private Long contractId;

    @NotNull
    @Column(name = "provider_id")
    private Long providerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ShippingStatus status;

    @NotNull
    @Column(name = "shipping_address_id")
    private Long shippingAddressId;

    @NotBlank
    @Column(name = "device_id")
    private String deviceId;

    @Column(name = "tracking_number")
    private String trackingNumber;

    @Column(name = "estimated_delivery_date")
    private LocalDate estimatedDeliveryDate;

    @Column(name = "delivery_time_window")
    private String deliveryTimeWindow;

    @Column(name = "delivery_options")
    private String deliveryOptions;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "delivery_rating")
    private Integer deliveryRating;

    @Column(name = "delivery_feedback")
    private String deliveryFeedback;

    @Column(name = "delivery_confirmed_at")
    private LocalDateTime deliveryConfirmedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_id", insertable = false, updatable = false)
    private LogisticsProvider provider;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = ShippingStatus.PENDING;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum ShippingStatus {
        PENDING,        // 配送準備中
        SHIPPED,        // 発送済み
        IN_TRANSIT,     // 配送中
        OUT_FOR_DELIVERY, // 配達中
        DELIVERED,      // 配達完了
        FAILED,         // 配達失敗
        RETURNED,       // 返送
        CANCELLED       // キャンセル
    }
}
