package com.ahamo.payment.service;

import com.ahamo.payment.dto.BillingScheduleDto;
import com.ahamo.payment.dto.PaymentRequestDto;
import com.ahamo.payment.dto.PaymentResponseDto;
import com.ahamo.payment.model.BillingSchedule;
import com.ahamo.payment.repository.BillingScheduleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecurringBillingServiceImpl implements RecurringBillingService {
    
    private final BillingScheduleRepository billingScheduleRepository;
    private final PaymentService paymentService;
    
    @Override
    @Transactional
    public void scheduleBilling(String customerId, BillingScheduleDto scheduleDto) {
        log.info("Scheduling billing for customer: {}", customerId);
        
        BillingSchedule schedule = BillingSchedule.builder()
                .scheduleUuid(UUID.randomUUID().toString())
                .customerId(customerId)
                .contractId(scheduleDto.getContractId())
                .amount(scheduleDto.getAmount())
                .frequency(scheduleDto.getFrequency())
                .nextBillingDate(scheduleDto.getNextBillingDate())
                .status(BillingSchedule.BillingStatus.ACTIVE)
                .paymentMethodId(scheduleDto.getPaymentMethodId())
                .retryCount(0)
                .maxRetryAttempts(3)
                .build();
        
        billingScheduleRepository.save(schedule);
        log.info("Billing scheduled with ID: {}", schedule.getScheduleUuid());
    }
    
    @Override
    @Transactional
    public BillingResult processBilling(String billingId) {
        log.info("Processing billing: {}", billingId);
        
        BillingSchedule schedule = billingScheduleRepository.findByScheduleUuid(billingId)
                .orElseThrow(() -> new RuntimeException("Billing schedule not found: " + billingId));
        
        try {
            PaymentRequestDto paymentRequest = PaymentRequestDto.builder()
                    .contractId(schedule.getContractId())
                    .paymentMethodId(schedule.getPaymentMethodId())
                    .build();
            
            PaymentResponseDto response = paymentService.processPayment(paymentRequest);
            
            if ("completed".equals(response.getStatus())) {
                updateSuccessfulBilling(schedule);
                return new BillingResult(true, "Billing processed successfully", response.getTransactionId());
            } else {
                handleBillingFailure(schedule, FailureReason.GATEWAY_ERROR);
                return new BillingResult(false, "Billing failed", null);
            }
            
        } catch (Exception e) {
            log.error("Billing processing failed for schedule: {}", billingId, e);
            handleBillingFailure(schedule, FailureReason.UNKNOWN);
            return new BillingResult(false, "Billing failed: " + e.getMessage(), null);
        }
    }
    
    @Override
    @Transactional
    public void handleFailedBilling(String billingId, FailureReason reason) {
        log.info("Handling failed billing: {} with reason: {}", billingId, reason);
        
        BillingSchedule schedule = billingScheduleRepository.findByScheduleUuid(billingId)
                .orElseThrow(() -> new RuntimeException("Billing schedule not found: " + billingId));
        
        handleBillingFailure(schedule, reason);
    }
    
    @Override
    @Transactional
    public void processScheduledBillings() {
        log.info("Processing scheduled billings");
        
        List<BillingSchedule> readySchedules = billingScheduleRepository
                .findSchedulesReadyForBilling(LocalDateTime.now());
        
        for (BillingSchedule schedule : readySchedules) {
            try {
                processBilling(schedule.getScheduleUuid());
            } catch (Exception e) {
                log.error("Failed to process scheduled billing: {}", schedule.getScheduleUuid(), e);
            }
        }
        
        log.info("Processed {} scheduled billings", readySchedules.size());
    }
    
    @Override
    @Transactional
    public void retryFailedBillings() {
        log.info("Retrying failed billings");
        
        List<BillingSchedule> failedSchedules = billingScheduleRepository.findFailedSchedulesForRetry();
        
        for (BillingSchedule schedule : failedSchedules) {
            try {
                LocalDateTime nextRetry = calculateNextRetryTime(schedule.getRetryCount());
                if (LocalDateTime.now().isAfter(nextRetry)) {
                    processBilling(schedule.getScheduleUuid());
                }
            } catch (Exception e) {
                log.error("Failed to retry billing: {}", schedule.getScheduleUuid(), e);
            }
        }
        
        log.info("Processed {} failed billing retries", failedSchedules.size());
    }
    
    private void updateSuccessfulBilling(BillingSchedule schedule) {
        schedule.setLastBillingDate(LocalDateTime.now());
        schedule.setNextBillingDate(calculateNextBillingDate(schedule));
        schedule.setRetryCount(0);
        schedule.setLastFailureReason(null);
        billingScheduleRepository.save(schedule);
    }
    
    private void handleBillingFailure(BillingSchedule schedule, FailureReason reason) {
        schedule.setRetryCount(schedule.getRetryCount() + 1);
        schedule.setLastFailureReason(reason.toString());
        
        if (schedule.getRetryCount() >= schedule.getMaxRetryAttempts()) {
            schedule.setStatus(BillingSchedule.BillingStatus.FAILED);
            log.warn("Billing schedule {} failed after {} attempts", 
                    schedule.getScheduleUuid(), schedule.getMaxRetryAttempts());
        }
        
        billingScheduleRepository.save(schedule);
    }
    
    private LocalDateTime calculateNextBillingDate(BillingSchedule schedule) {
        LocalDateTime current = schedule.getNextBillingDate();
        
        switch (schedule.getFrequency()) {
            case MONTHLY:
                return current.plusMonths(1);
            case QUARTERLY:
                return current.plusMonths(3);
            case YEARLY:
                return current.plusYears(1);
            default:
                return current.plusMonths(1);
        }
    }
    
    private LocalDateTime calculateNextRetryTime(int retryCount) {
        int delayMinutes = (int) Math.pow(2, retryCount) * 5;
        return LocalDateTime.now().plusMinutes(delayMinutes);
    }
}
