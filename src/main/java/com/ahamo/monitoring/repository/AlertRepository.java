package com.ahamo.monitoring.repository;

import com.ahamo.monitoring.model.Alert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AlertRepository extends JpaRepository<Alert, Long> {
    
    List<Alert> findByStatus(String status);
    
    List<Alert> findByAlertType(String alertType);
    
    List<Alert> findBySeverity(String severity);
    
    @Query("SELECT a FROM Alert a WHERE a.createdAt >= :startTime AND a.createdAt <= :endTime")
    List<Alert> findByCreatedAtBetween(@Param("startTime") LocalDateTime startTime, 
                                       @Param("endTime") LocalDateTime endTime);
    
    @Query("SELECT a FROM Alert a WHERE a.source = :source AND a.alertType = :alertType AND a.status = :status")
    List<Alert> findBySourceAndAlertTypeAndStatus(@Param("source") String source, 
                                                   @Param("alertType") String alertType, 
                                                   @Param("status") String status);
    
    boolean existsBySourceAndAlertTypeAndStatus(String source, String alertType, String status);
    
    @Query("SELECT COUNT(a) FROM Alert a WHERE a.status = 'ACTIVE'")
    long countActiveAlerts();
    
    @Query("SELECT COUNT(a) FROM Alert a WHERE a.severity = 'CRITICAL' AND a.status = 'ACTIVE'")
    long countCriticalAlerts();
}
