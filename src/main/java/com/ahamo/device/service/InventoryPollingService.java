package com.ahamo.device.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryPollingService {
    
    private final DeviceService deviceService;
    
    @Scheduled(cron = "0 0 2 * * ?")
    public void updateInventoryStatus() {
        log.info("Starting daily inventory status update");
        try {
            deviceService.updateInventoryStatus();
            log.info("Daily inventory status update completed successfully");
        } catch (Exception e) {
            log.error("Failed to update inventory status", e);
        }
    }
}
