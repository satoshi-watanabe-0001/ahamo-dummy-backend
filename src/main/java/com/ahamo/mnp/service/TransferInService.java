package com.ahamo.mnp.service;

import com.ahamo.mnp.dto.TransferInRequest;
import com.ahamo.mnp.dto.TransferInResponse;

public interface TransferInService {
    
    TransferInResponse processTransferIn(TransferInRequest request);
    
    TransferInResponse getTransferStatus(String transferId);
    
    void processInProgressTransfers();
}
