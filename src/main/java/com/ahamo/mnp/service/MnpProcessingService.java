package com.ahamo.mnp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MnpProcessingService {
    
    private final MnpService mnpService;
    private final TransferInService transferInService;
    private final TransferOutService transferOutService;
    
    @Scheduled(fixedRate = 300000)
    public void processPendingMnpRequests() {
        log.info("Starting scheduled MNP request processing");
        try {
            mnpService.processPendingRequests();
            log.info("MNP request processing completed successfully");
        } catch (Exception e) {
            log.error("Failed to process pending MNP requests", e);
        }
    }
    
    @Scheduled(fixedRate = 600000)
    public void processTransferRequests() {
        log.info("Starting scheduled transfer request processing");
        try {
            transferInService.processInProgressTransfers();
            transferOutService.processOutgoingTransfers();
            log.info("Transfer request processing completed successfully");
        } catch (Exception e) {
            log.error("Failed to process transfer requests", e);
        }
    }
}
