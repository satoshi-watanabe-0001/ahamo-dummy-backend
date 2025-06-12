package com.ahamo.mnp.integration;

import com.ahamo.mnp.dto.MnpEligibilityRequest;
import com.ahamo.mnp.dto.MnpEligibilityResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@Slf4j
public class SoftbankApiClient implements CarrierApiClient {

    @Override
    public String getCarrierCode() {
        return "SOFTBANK";
    }

    @Override
    public MnpEligibilityResponse checkEligibility(MnpEligibilityRequest request) {
        log.info("Checking MNP eligibility with Softbank for phone: {}", request.getPhoneNumber());
        
        try {
            Thread.sleep(800);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        return MnpEligibilityResponse.builder()
                .eligible(true)
                .phoneNumber(request.getPhoneNumber())
                .currentCarrier(request.getCurrentCarrier())
                .estimatedPortingTime("1営業日")
                .additionalRequirements(Arrays.asList("本人確認書類"))
                .restrictions(Arrays.asList())
                .build();
    }

    @Override
    public String requestReservationNumber(String phoneNumber, String accountInfo) {
        log.info("Requesting MNP reservation number from Softbank for phone: {}", phoneNumber);
        
        try {
            Thread.sleep(1800);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        return "SB" + System.currentTimeMillis() % 100000;
    }

    @Override
    public boolean confirmTransfer(String phoneNumber, String reservationNumber) {
        log.info("Confirming transfer with Softbank for phone: {} with reservation: {}", phoneNumber, reservationNumber);
        
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        return true;
    }

    @Override
    public boolean cancelTransfer(String phoneNumber, String reservationNumber) {
        log.info("Cancelling transfer with Softbank for phone: {} with reservation: {}", phoneNumber, reservationNumber);
        return true;
    }

    @Override
    public String getTransferStatus(String phoneNumber, String reservationNumber) {
        log.info("Getting transfer status from Softbank for phone: {} with reservation: {}", phoneNumber, reservationNumber);
        return "IN_PROGRESS";
    }
}
