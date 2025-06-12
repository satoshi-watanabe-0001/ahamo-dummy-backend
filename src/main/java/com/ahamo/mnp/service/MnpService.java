package com.ahamo.mnp.service;

import com.ahamo.mnp.dto.*;

public interface MnpService {
    
    MnpEligibilityResponse checkEligibility(MnpEligibilityRequest request);
    
    MnpResponse submitMnpRequest(com.ahamo.mnp.dto.MnpRequest request);
    
    MnpResponse getMnpStatus(String mnpId);
    
    void processPendingRequests();
}
