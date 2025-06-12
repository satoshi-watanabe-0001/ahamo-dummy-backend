package com.ahamo.mnp.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "mnp_status_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MnpStatusHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "mnp_request_id")
    private Long mnpRequestId;

    @Enumerated(EnumType.STRING)
    @Column(name = "from_status")
    private MnpRequest.MnpStatus fromStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "to_status")
    private MnpRequest.MnpStatus toStatus;

    @Column(name = "reason")
    private String reason;

    @Column(name = "error_message")
    private String errorMessage;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "created_by")
    private String createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mnp_request_id", insertable = false, updatable = false)
    private MnpRequest mnpRequest;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
