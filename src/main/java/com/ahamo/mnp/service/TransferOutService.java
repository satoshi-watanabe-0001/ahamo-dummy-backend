package com.ahamo.mnp.service;

import com.ahamo.mnp.dto.TransferOutRequest;
import com.ahamo.mnp.dto.TransferOutResponse;

public interface TransferOutService {
    
    TransferOutResponse processTransferOut(TransferOutRequest request);
    
    TransferOutResponse getTransferStatus(String transferId);
    
    void processOutgoingTransfers();
}
