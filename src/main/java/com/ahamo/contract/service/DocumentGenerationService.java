package com.ahamo.contract.service;

import com.ahamo.contract.dto.ContractGenerationRequest;
import com.ahamo.contract.dto.ContractGenerationResponse;

public interface DocumentGenerationService {

    ContractGenerationResponse generateContract(String contractId, ContractGenerationRequest request, String userId);

    String generateDocumentContent(String templateContent, Object variables);

    byte[] generatePdfDocument(String htmlContent);

    String storeDocument(String contractId, byte[] documentContent, String format);
}
