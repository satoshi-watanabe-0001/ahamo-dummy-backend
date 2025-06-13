package com.ahamo.shipping.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "tracking_events")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrackingEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "shipping_order_id")
    private Long shippingOrderId;

    @NotBlank
    @Column(name = "status")
    private String status;

    @Column(name = "location")
    private String location;

    @NotNull
    @Column(name = "timestamp")
    private LocalDateTime timestamp;

    @Column(name = "description")
    private String description;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipping_order_id", insertable = false, updatable = false)
    private ShippingOrder shippingOrder;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
