package com.ahamo.mnp.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "mnp_requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MnpRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "mnp_id", unique = true)
    private String mnpId;

    @Column(name = "contract_id")
    private String contractId;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "current_carrier")
    private String currentCarrier;

    @Column(name = "account_name")
    private String accountName;

    @Column(name = "account_number")
    private String accountNumber;

    @Column(name = "account_password")
    private String accountPassword;

    @Column(name = "reservation_number")
    private String reservationNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private MnpStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private MnpType type;

    @Column(name = "desired_porting_date")
    private LocalDate desiredPortingDate;

    @Column(name = "estimated_completion_date")
    private LocalDate estimatedCompletionDate;

    @Column(name = "actual_completion_date")
    private LocalDate actualCompletionDate;

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
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum MnpStatus {
        PENDING, IN_PROGRESS, COMPLETED, FAILED, CANCELLED
    }

    public enum MnpType {
        TRANSFER_IN, TRANSFER_OUT, ELIGIBILITY_CHECK
    }
}
