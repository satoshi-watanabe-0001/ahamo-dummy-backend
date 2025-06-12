package com.ahamo.contract.service;

import com.ahamo.contract.dto.ContractGenerationRequest;
import com.ahamo.contract.dto.ContractGenerationResponse;
import com.ahamo.contract.model.Contract;
import com.ahamo.contract.model.ContractTemplate;
import com.ahamo.contract.repository.ContractRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DocumentGenerationServiceImpl implements DocumentGenerationService {

    private final ContractRepository contractRepository;
    private final TemplateService templateService;
    private final DocumentStorageService documentStorageService;

    @Override
    public ContractGenerationResponse generateContract(String contractId, ContractGenerationRequest request, String userId) {
        log.info("Generating contract document for contract: {}", contractId);

        Contract contract = contractRepository.findByContractUuid(contractId)
            .orElseThrow(() -> new IllegalArgumentException("Contract not found: " + contractId));

        ContractTemplate template = templateService.getTemplateById(request.getTemplateId())
            .orElseThrow(() -> new IllegalArgumentException("Template not found: " + request.getTemplateId()));

        String documentContent = generateDocumentContent(template.getContent(), request.getVariables());

        byte[] documentBytes;
        String format = request.getFormat() != null ? request.getFormat() : "pdf";
        
        if ("pdf".equalsIgnoreCase(format)) {
            documentBytes = generatePdfDocument(documentContent);
        } else {
            documentBytes = documentContent.getBytes();
        }

        String documentId = UUID.randomUUID().toString();
        String documentUrl = documentStorageService.storeDocument(documentId, documentBytes, format);

        contract.setDocumentId(documentId);
        contract.setDocumentUrl(documentUrl);
        contract.setTemplateId(request.getTemplateId());
        contract.setUpdatedBy(userId);
        contractRepository.save(contract);

        ContractGenerationResponse response = ContractGenerationResponse.builder()
            .documentId(documentId)
            .documentUrl(documentUrl)
            .format(format)
            .generatedAt(LocalDateTime.now())
            .expiresAt(LocalDateTime.now().plus(24, ChronoUnit.HOURS))
            .build();

        log.info("Generated contract document with ID: {}", documentId);
        return response;
    }

    @Override
    public String generateDocumentContent(String templateContent, Object variables) {
        log.debug("Generating document content from template");
        
        if (variables == null) {
            return templateContent;
        }

        String content = templateContent;
        
        if (variables instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> variableMap = (Map<String, Object>) variables;
            
            Pattern pattern = Pattern.compile("\\{\\{([^}]+)\\}\\}");
            Matcher matcher = pattern.matcher(content);
            
            StringBuffer result = new StringBuffer();
            while (matcher.find()) {
                String variableName = matcher.group(1).trim();
                Object value = variableMap.get(variableName);
                String replacement = value != null ? value.toString() : "";
                matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
            }
            matcher.appendTail(result);
            content = result.toString();
        }

        return content;
    }

    @Override
    public byte[] generatePdfDocument(String htmlContent) {
        log.debug("Converting HTML content to PDF");
        
        try {
            return htmlContent.getBytes("UTF-8");
        } catch (Exception e) {
            log.error("Failed to generate PDF document", e);
            throw new RuntimeException("PDF generation failed", e);
        }
    }

    @Override
    public String storeDocument(String contractId, byte[] documentContent, String format) {
        return documentStorageService.storeDocument(contractId, documentContent, format);
    }
}
