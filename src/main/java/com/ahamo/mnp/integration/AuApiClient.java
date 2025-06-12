package com.ahamo.mnp.integration;

import com.ahamo.mnp.dto.MnpEligibilityRequest;
import com.ahamo.mnp.dto.MnpEligibilityResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@Slf4j
public class AuApiClient implements CarrierApiClient {

    @Override
    public String getCarrierCode() {
        return "AU";
    }

    @Override
    public MnpEligibilityResponse checkEligibility(MnpEligibilityRequest request) {
        log.info("Checking MNP eligibility with AU for phone: {}", request.getPhoneNumber());
        
        try {
            Thread.sleep(1200);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        return MnpEligibilityResponse.builder()
                .eligible(true)
                .phoneNumber(request.getPhoneNumber())
                .currentCarrier(request.getCurrentCarrier())
                .estimatedPortingTime("2-3営業日")
                .additionalRequirements(Arrays.asList("本人確認書類", "契約者情報", "支払い情報"))
                .restrictions(Arrays.asList())
                .build();
    }

    @Override
    public String requestReservationNumber(String phoneNumber, String accountInfo) {
        log.info("Requesting MNP reservation number from AU for phone: {}", phoneNumber);
        
        try {
            Thread.sleep(2500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        return "AU" + System.currentTimeMillis() % 100000;
    }

    @Override
    public boolean confirmTransfer(String phoneNumber, String reservationNumber) {
        log.info("Confirming transfer with AU for phone: {} with reservation: {}", phoneNumber, reservationNumber);
        
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        return true;
    }

    @Override
    public boolean cancelTransfer(String phoneNumber, String reservationNumber) {
        log.info("Cancelling transfer with AU for phone: {} with reservation: {}", phoneNumber, reservationNumber);
        return true;
    }

    @Override
    public String getTransferStatus(String phoneNumber, String reservationNumber) {
        log.info("Getting transfer status from AU for phone: {} with reservation: {}", phoneNumber, reservationNumber);
        return "IN_PROGRESS";
    }
}
