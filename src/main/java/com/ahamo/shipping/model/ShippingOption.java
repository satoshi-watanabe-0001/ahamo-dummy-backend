package com.ahamo.shipping.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Entity
@Table(name = "shipping_options")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShippingOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "option_code", unique = true)
    private String optionCode;

    @NotBlank
    @Column(name = "option_name")
    private String optionName;

    @Column(name = "description")
    private String description;

    @Column(name = "requires_recipient_info")
    private Boolean requiresRecipientInfo = false;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
