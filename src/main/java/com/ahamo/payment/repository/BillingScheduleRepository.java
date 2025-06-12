package com.ahamo.payment.repository;

import com.ahamo.payment.model.BillingSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BillingScheduleRepository extends JpaRepository<BillingSchedule, Long> {
    
    Optional<BillingSchedule> findByScheduleUuid(String scheduleUuid);
    
    List<BillingSchedule> findByCustomerId(String customerId);
    
    List<BillingSchedule> findByStatus(BillingSchedule.BillingStatus status);
    
    @Query("SELECT bs FROM BillingSchedule bs WHERE bs.nextBillingDate <= :currentTime AND bs.status = 'ACTIVE'")
    List<BillingSchedule> findSchedulesReadyForBilling(LocalDateTime currentTime);
    
    @Query("SELECT bs FROM BillingSchedule bs WHERE bs.status = 'FAILED' AND bs.retryCount < bs.maxRetryAttempts")
    List<BillingSchedule> findFailedSchedulesForRetry();
}
