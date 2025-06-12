package com.ahamo.contract.repository;

import com.ahamo.contract.model.ContractAuditEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ContractAuditEventRepository extends JpaRepository<ContractAuditEvent, Long> {

    List<ContractAuditEvent> findByContractIdOrderByTimestampDesc(String contractId);

    List<ContractAuditEvent> findByEventTypeAndTimestampBetween(
        ContractAuditEvent.EventType eventType, 
        LocalDateTime startTime, 
        LocalDateTime endTime
    );

    @Query("SELECT e FROM ContractAuditEvent e WHERE e.contractId = :contractId AND e.eventType IN :eventTypes ORDER BY e.timestamp DESC")
    List<ContractAuditEvent> findByContractIdAndEventTypes(
        @Param("contractId") String contractId, 
        @Param("eventTypes") List<ContractAuditEvent.EventType> eventTypes
    );

    List<ContractAuditEvent> findByUserIdAndTimestampBetween(String userId, LocalDateTime startTime, LocalDateTime endTime);
}
