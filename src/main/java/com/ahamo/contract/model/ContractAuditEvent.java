package com.ahamo.contract.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Entity
@Table(name = "contract_audit_events")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContractAuditEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_uuid", unique = true)
    private String eventUuid;

    @NotBlank
    @Column(name = "contract_id")
    private String contractId;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type")
    private EventType eventType;

    @Column(name = "timestamp")
    private LocalDateTime timestamp;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;

    @Column(name = "details", columnDefinition = "JSON")
    private String details;

    @Column(name = "certificate_id")
    private String certificateId;

    @Column(name = "timestamp_token", columnDefinition = "TEXT")
    private String timestampToken;

    @Column(name = "hash_value")
    private String hashValue;

    @PrePersist
    protected void onCreate() {
        if (timestamp == null) {
            timestamp = LocalDateTime.now();
        }
    }

    public enum EventType {
        CREATED, UPDATED, GENERATED, SIGNED, VIEWED, DOWNLOADED
    }
}
