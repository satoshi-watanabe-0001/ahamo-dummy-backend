package com.ahamo.contract.service;

import com.ahamo.contract.dto.ElectronicSignatureRequest;
import com.ahamo.contract.dto.ElectronicSignatureResponse;

public interface ElectronicSignatureService {

    ElectronicSignatureResponse initiateSignature(String contractId, ElectronicSignatureRequest request, String userId);

    ElectronicSignatureResponse getSignatureStatus(String signatureId);

    void processSignatureCallback(String signatureId, String status, String certificateInfo);

    boolean verifySignature(String contractId, String signatureId);

    String generateTimestamp(String documentHash);
}
