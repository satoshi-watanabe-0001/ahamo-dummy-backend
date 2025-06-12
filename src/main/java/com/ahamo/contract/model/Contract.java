package com.ahamo.contract.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "contracts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Contract {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "contract_uuid", unique = true)
    private String contractUuid;

    @NotBlank
    @Column(name = "plan_id")
    private String planId;

    @NotBlank
    @Column(name = "device_id")
    private String deviceId;

    @Column(name = "device_color")
    private String deviceColor;

    @Column(name = "device_storage")
    private String deviceStorage;

    @ElementCollection
    @CollectionTable(name = "contract_options", joinColumns = @JoinColumn(name = "contract_id"))
    @Column(name = "option_id")
    private List<String> options;

    @Column(name = "customer_first_name")
    private String customerFirstName;

    @Column(name = "customer_last_name")
    private String customerLastName;

    @Column(name = "customer_first_name_kana")
    private String customerFirstNameKana;

    @Column(name = "customer_last_name_kana")
    private String customerLastNameKana;

    @Column(name = "customer_birth_date")
    private String customerBirthDate;

    @Column(name = "customer_gender")
    private String customerGender;

    @Column(name = "customer_phone")
    private String customerPhone;

    @Column(name = "customer_email")
    private String customerEmail;

    @Column(name = "customer_postal_code")
    private String customerPostalCode;

    @Column(name = "customer_prefecture")
    private String customerPrefecture;

    @Column(name = "customer_city")
    private String customerCity;

    @Column(name = "customer_address_line1")
    private String customerAddressLine1;

    @Column(name = "customer_address_line2")
    private String customerAddressLine2;

    @Column(name = "customer_building")
    private String customerBuilding;

    @Column(name = "total_amount", precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ContractStatus status;

    @Column(name = "ekyc_session_id")
    private String ekycSessionId;

    @Column(name = "ekyc_status")
    @Enumerated(EnumType.STRING)
    private EkycStatus ekycStatus;

    @Column(name = "electronic_signature")
    private String electronicSignature;

    @Column(name = "signature_id")
    private String signatureId;

    @Column(name = "signature_status")
    @Enumerated(EnumType.STRING)
    private SignatureStatus signatureStatus;

    @Column(name = "document_id")
    private String documentId;

    @Column(name = "document_url")
    private String documentUrl;

    @Column(name = "template_id")
    private String templateId;

    @Column(name = "confirmation_number")
    private String confirmationNumber;

    @Column(name = "signed_at")
    private LocalDateTime signedAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "updated_by")
    private String updatedBy;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = ContractStatus.DRAFT;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum ContractStatus {
        DRAFT, PENDING, SIGNED, ACTIVE, CANCELLED
    }

    public enum EkycStatus {
        PENDING, IN_PROGRESS, COMPLETED, FAILED, EXPIRED
    }

    public enum SignatureStatus {
        UNSIGNED, PENDING, SIGNED, VERIFIED, FAILED
    }
}
