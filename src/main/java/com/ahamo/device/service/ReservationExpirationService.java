package com.ahamo.device.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationExpirationService {
    
    private final ReservationService reservationService;
    
    @Scheduled(fixedRate = 300000)
    public void processExpiredReservations() {
        log.info("Starting expired reservations cleanup");
        try {
            reservationService.processExpiredReservations();
            log.info("Expired reservations cleanup completed successfully");
        } catch (Exception e) {
            log.error("Failed to process expired reservations", e);
        }
    }
}
