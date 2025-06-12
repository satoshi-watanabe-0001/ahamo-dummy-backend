package com.ahamo.contract.service;

import com.ahamo.contract.dto.ElectronicSignatureRequest;
import com.ahamo.contract.dto.ElectronicSignatureResponse;
import com.ahamo.contract.model.Contract;
import com.ahamo.contract.repository.ContractRepository;
import com.ahamo.contract.adapter.DocuSignAdapter;
import com.ahamo.contract.adapter.TimestampServiceAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ElectronicSignatureServiceImpl implements ElectronicSignatureService {

    private final ContractRepository contractRepository;
    private final DocuSignAdapter docuSignAdapter;
    private final TimestampServiceAdapter timestampServiceAdapter;
    private final ContractAuditService auditService;

    @Override
    public ElectronicSignatureResponse initiateSignature(String contractId, ElectronicSignatureRequest request, String userId) {
        log.info("Initiating electronic signature for contract: {}", contractId);

        Contract contract = contractRepository.findByContractUuid(contractId)
            .orElseThrow(() -> new IllegalArgumentException("Contract not found: " + contractId));

        if (contract.getDocumentId() == null) {
            throw new IllegalStateException("Contract document must be generated before signing");
        }

        String signatureId = UUID.randomUUID().toString();
        
        ElectronicSignatureResponse response;
        
        switch (request.getSignatureMethod().toLowerCase()) {
            case "embedded":
                response = initiateEmbeddedSignature(contract, request, signatureId);
                break;
            case "remote":
                response = initiateRemoteSignature(contract, request, signatureId);
                break;
            case "cloud":
                response = initiateCloudSignature(contract, request, signatureId);
                break;
            default:
                throw new IllegalArgumentException("Unsupported signature method: " + request.getSignatureMethod());
        }

        contract.setSignatureId(signatureId);
        contract.setSignatureStatus(Contract.SignatureStatus.PENDING);
        contract.setUpdatedBy(userId);
        contractRepository.save(contract);

        auditService.logEvent(contractId, "SIGNATURE_INITIATED", userId, 
            Map.of("signatureMethod", request.getSignatureMethod(), "signatureId", signatureId));

        log.info("Electronic signature initiated with ID: {}", signatureId);
        return response;
    }

    @Override
    public ElectronicSignatureResponse getSignatureStatus(String signatureId) {
        log.info("Getting signature status for: {}", signatureId);
        
        return docuSignAdapter.getSignatureStatus(signatureId);
    }

    @Override
    public void processSignatureCallback(String signatureId, String status, String certificateInfo) {
        log.info("Processing signature callback for: {} with status: {}", signatureId, status);

        Contract contract = contractRepository.findBySignatureId(signatureId)
            .orElseThrow(() -> new IllegalArgumentException("Contract not found for signature: " + signatureId));

        Contract.SignatureStatus signatureStatus;
        switch (status.toLowerCase()) {
            case "completed":
                signatureStatus = Contract.SignatureStatus.SIGNED;
                contract.setSignedAt(LocalDateTime.now());
                break;
            case "failed":
                signatureStatus = Contract.SignatureStatus.FAILED;
                break;
            default:
                signatureStatus = Contract.SignatureStatus.PENDING;
        }

        contract.setSignatureStatus(signatureStatus);
        contract.setElectronicSignature(certificateInfo);
        contractRepository.save(contract);

        auditService.logEvent(contract.getContractUuid(), "SIGNATURE_" + status.toUpperCase(), 
            "system", Map.of("signatureId", signatureId, "certificateInfo", certificateInfo));

        log.info("Signature callback processed for contract: {}", contract.getContractUuid());
    }

    @Override
    public boolean verifySignature(String contractId, String signatureId) {
        log.info("Verifying signature for contract: {}", contractId);
        
        Contract contract = contractRepository.findByContractUuid(contractId)
            .orElseThrow(() -> new IllegalArgumentException("Contract not found: " + contractId));

        if (!signatureId.equals(contract.getSignatureId())) {
            return false;
        }

        boolean isValid = docuSignAdapter.verifySignature(signatureId);
        
        if (isValid) {
            contract.setSignatureStatus(Contract.SignatureStatus.VERIFIED);
            contractRepository.save(contract);
            
            auditService.logEvent(contractId, "SIGNATURE_VERIFIED", "system", 
                Map.of("signatureId", signatureId));
        }

        return isValid;
    }

    @Override
    public String generateTimestamp(String documentHash) {
        log.info("Generating timestamp for document hash");
        return timestampServiceAdapter.generateTimestamp(documentHash);
    }

    private ElectronicSignatureResponse initiateEmbeddedSignature(Contract contract, 
            ElectronicSignatureRequest request, String signatureId) {
        
        String signatureUrl = docuSignAdapter.createEmbeddedSignature(
            contract.getDocumentUrl(), 
            request.getSignerInfo(), 
            signatureId,
            request.getReturnUrl()
        );

        return ElectronicSignatureResponse.builder()
            .signatureId(signatureId)
            .signatureUrl(signatureUrl)
            .status("pending")
            .expiresAt(LocalDateTime.now().plus(24, ChronoUnit.HOURS))
            .build();
    }

    private ElectronicSignatureResponse initiateRemoteSignature(Contract contract, 
            ElectronicSignatureRequest request, String signatureId) {
        
        String signatureUrl = docuSignAdapter.createRemoteSignature(
            contract.getDocumentUrl(), 
            request.getSignerInfo(), 
            signatureId,
            request.getWebhookUrl()
        );

        return ElectronicSignatureResponse.builder()
            .signatureId(signatureId)
            .signatureUrl(signatureUrl)
            .status("pending")
            .expiresAt(LocalDateTime.now().plus(7, ChronoUnit.DAYS))
            .build();
    }

    private ElectronicSignatureResponse initiateCloudSignature(Contract contract, 
            ElectronicSignatureRequest request, String signatureId) {
        
        Map<String, Object> certificateInfo = docuSignAdapter.createCloudSignature(
            contract.getDocumentUrl(), 
            request.getSignerInfo(), 
            signatureId
        );

        return ElectronicSignatureResponse.builder()
            .signatureId(signatureId)
            .status("completed")
            .signedAt(LocalDateTime.now())
            .certificateInfo(certificateInfo)
            .build();
    }
}
