package com.ahamo.payment.scheduler;

import com.ahamo.payment.service.RecurringBillingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class BillingScheduler {
    
    private final RecurringBillingService recurringBillingService;
    
    @Scheduled(fixedRate = 300000)
    public void processScheduledBillings() {
        log.info("Starting scheduled billing processing");
        try {
            recurringBillingService.processScheduledBillings();
            log.info("Scheduled billing processing completed successfully");
        } catch (Exception e) {
            log.error("Failed to process scheduled billings", e);
        }
    }
    
    @Scheduled(fixedRate = 600000)
    public void retryFailedBillings() {
        log.info("Starting failed billing retry processing");
        try {
            recurringBillingService.retryFailedBillings();
            log.info("Failed billing retry processing completed successfully");
        } catch (Exception e) {
            log.error("Failed to process billing retries", e);
        }
    }
}
