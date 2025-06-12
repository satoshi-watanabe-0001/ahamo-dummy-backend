package com.ahamo.mnp.integration;

import com.ahamo.mnp.dto.MnpEligibilityRequest;
import com.ahamo.mnp.dto.MnpEligibilityResponse;

public interface CarrierApiClient {
    
    String getCarrierCode();
    
    MnpEligibilityResponse checkEligibility(MnpEligibilityRequest request);
    
    String requestReservationNumber(String phoneNumber, String accountInfo);
    
    boolean confirmTransfer(String phoneNumber, String reservationNumber);
    
    boolean cancelTransfer(String phoneNumber, String reservationNumber);
    
    String getTransferStatus(String phoneNumber, String reservationNumber);
}
