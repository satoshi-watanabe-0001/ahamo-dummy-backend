package com.ahamo.payment.model;

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
@Table(name = "billing_schedules")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BillingSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "schedule_uuid", unique = true)
    private String scheduleUuid;

    @NotBlank
    @Column(name = "customer_id")
    private String customerId;

    @NotBlank
    @Column(name = "contract_id")
    private String contractId;

    @NotNull
    @Column(name = "amount", precision = 10, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "frequency")
    private BillingFrequency frequency;

    @Column(name = "next_billing_date")
    private LocalDateTime nextBillingDate;

    @Column(name = "last_billing_date")
    private LocalDateTime lastBillingDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private BillingStatus status;

    @Column(name = "payment_method_id")
    private String paymentMethodId;

    @Column(name = "retry_count")
    private Integer retryCount;

    @Column(name = "max_retry_attempts")
    private Integer maxRetryAttempts;

    @Column(name = "last_failure_reason")
    private String lastFailureReason;

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
            status = BillingStatus.ACTIVE;
        }
        if (retryCount == null) {
            retryCount = 0;
        }
        if (maxRetryAttempts == null) {
            maxRetryAttempts = 3;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum BillingFrequency {
        MONTHLY, QUARTERLY, YEARLY
    }

    public enum BillingStatus {
        ACTIVE, SUSPENDED, CANCELLED, FAILED
    }
}
