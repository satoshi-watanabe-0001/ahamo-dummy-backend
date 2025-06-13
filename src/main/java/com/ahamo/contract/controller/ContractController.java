package com.ahamo.contract.controller;

import com.ahamo.contract.dto.*;
import com.ahamo.contract.model.Contract;
import com.ahamo.contract.model.ContractChangeHistory;
import com.ahamo.contract.model.ContractTemplate;
import com.ahamo.contract.service.*;
import com.ahamo.plan.dto.FeeCalculationResult;
import com.ahamo.plan.dto.OptionResponse;
import com.ahamo.plan.dto.PlanResponse;
import com.ahamo.plan.model.Plan;
import com.ahamo.option.model.Option;
import com.ahamo.plan.service.PlanService;
import com.ahamo.option.service.OptionService;
import com.ahamo.plan.service.FeeCalculationService;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/contracts")
@RequiredArgsConstructor
@Slf4j
public class ContractController {

    private final TemplateService templateService;
    private final DocumentGenerationService documentGenerationService;
    private final ElectronicSignatureService electronicSignatureService;
    private final DocumentStorageService documentStorageService;
    private final ContractAuditService auditService;
    private final ContractService contractService;
    private final PlanService planService;
    private final OptionService optionService;
    private final FeeCalculationService feeCalculationService;

    @GetMapping("/templates")
    public ResponseEntity<List<ContractTemplate>> getTemplates() {
        log.info("Getting all contract templates");
        List<ContractTemplate> templates = templateService.getAllActiveTemplates();
        return ResponseEntity.ok(templates);
    }

    @PostMapping("/templates")
    public ResponseEntity<ContractTemplate> createTemplate(@Valid @RequestBody ContractTemplateCreateRequest request) {
        log.info("Creating contract template: {}", request.getName());
        String userId = getCurrentUserId();
        ContractTemplate template = templateService.createTemplate(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(template);
    }

    @GetMapping("/templates/{templateId}")
    public ResponseEntity<ContractTemplate> getTemplate(@PathVariable String templateId) {
        log.info("Getting contract template: {}", templateId);
        return templateService.getTemplateById(templateId)
            .map(template -> ResponseEntity.ok(template))
            .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/templates/{templateId}")
    public ResponseEntity<ContractTemplate> updateTemplate(
            @PathVariable String templateId,
            @Valid @RequestBody ContractTemplateUpdateRequest request) {
        log.info("Updating contract template: {}", templateId);
        String userId = getCurrentUserId();
        ContractTemplate template = templateService.updateTemplate(templateId, request, userId);
        return ResponseEntity.ok(template);
    }

    @PostMapping("/{contractId}/generate")
    public ResponseEntity<ContractGenerationResponse> generateContract(
            @PathVariable String contractId,
            @Valid @RequestBody ContractGenerationRequest request) {
        log.info("Generating contract document for: {}", contractId);
        String userId = getCurrentUserId();
        ContractGenerationResponse response = documentGenerationService.generateContract(contractId, request, userId);
        
        auditService.logEvent(contractId, "GENERATED", userId, 
            Map.of("templateId", request.getTemplateId(), "format", request.getFormat()));
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{contractId}/sign")
    public ResponseEntity<ElectronicSignatureResponse> signContract(
            @PathVariable String contractId,
            @Valid @RequestBody ElectronicSignatureRequest request) {
        log.info("Initiating electronic signature for contract: {}", contractId);
        String userId = getCurrentUserId();
        ElectronicSignatureResponse response = electronicSignatureService.initiateSignature(contractId, request, userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{contractId}/document")
    public ResponseEntity<ContractDocumentResponse> getContractDocument(
            @PathVariable String contractId,
            @RequestParam(defaultValue = "pdf") String format) {
        log.info("Getting contract document for: {} in format: {}", contractId, format);
        String userId = getCurrentUserId();
        
        Contract contract = contractService.getContractById(contractId)
            .orElseThrow(() -> new IllegalArgumentException("Contract not found: " + contractId));

        if (contract.getDocumentId() == null) {
            return ResponseEntity.notFound().build();
        }

        String documentUrl = documentStorageService.generateAccessUrl(contract.getDocumentId(), 24);
        
        ContractDocumentResponse response = ContractDocumentResponse.builder()
            .documentId(contract.getDocumentId())
            .contractId(contractId)
            .documentUrl(documentUrl)
            .format(format)
            .signatureStatus(contract.getSignatureStatus().name().toLowerCase())
            .createdAt(contract.getCreatedAt())
            .signedAt(contract.getSignedAt())
            .expiresAt(LocalDateTime.now().plusHours(24))
            .build();

        auditService.logEvent(contractId, "VIEWED", userId, 
            Map.of("documentId", contract.getDocumentId(), "format", format));

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{contractId}/audit-trail")
    public ResponseEntity<ContractAuditTrailResponse> getAuditTrail(@PathVariable String contractId) {
        log.info("Getting audit trail for contract: {}", contractId);
        ContractAuditTrailResponse response = auditService.getAuditTrail(contractId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<ContractSearchResponse> searchContracts(
            @RequestParam(required = false) String customerName,
            @RequestParam(required = false) String contractNumber,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int limit) {
        
        log.info("Searching contracts with filters - customerName: {}, status: {}", customerName, status);
        
        if (limit > 100) {
            limit = 100;
        }
        
        Pageable pageable = PageRequest.of(page - 1, limit);
        LocalDateTime dateTimeFrom = dateFrom != null ? dateFrom.atStartOfDay() : null;
        LocalDateTime dateTimeTo = dateTo != null ? dateTo.atTime(23, 59, 59) : null;
        
        Contract.ContractStatus contractStatus = null;
        if (status != null) {
            try {
                contractStatus = Contract.ContractStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                log.warn("Invalid status parameter: {}", status);
            }
        }
        
        Page<Contract> contractPage = contractService.searchContracts(
            customerName, contractNumber, contractStatus, dateTimeFrom, dateTimeTo, pageable);
        
        List<ContractSearchResponse.ContractSummary> contractSummaries = contractPage.getContent().stream()
            .map(this::convertToContractSummary)
            .collect(Collectors.toList());
        
        ContractSearchResponse.Pagination pagination = ContractSearchResponse.Pagination.builder()
            .currentPage(page)
            .totalPages(contractPage.getTotalPages())
            .totalCount((int) contractPage.getTotalElements())
            .perPage(limit)
            .build();
        
        ContractSearchResponse response = ContractSearchResponse.builder()
            .contracts(contractSummaries)
            .pagination(pagination)
            .build();
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/signature/webhook")
    public ResponseEntity<Void> handleSignatureWebhook(
            @RequestParam String signatureId,
            @RequestParam String status,
            @RequestBody(required = false) String certificateInfo) {
        log.info("Received signature webhook for: {} with status: {}", signatureId, status);
        
        try {
            electronicSignatureService.processSignatureCallback(signatureId, status, certificateInfo);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Failed to process signature webhook", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private ContractSearchResponse.ContractSummary convertToContractSummary(Contract contract) {
        String customerName = contract.getCustomerFirstName() + " " + contract.getCustomerLastName();
        
        return ContractSearchResponse.ContractSummary.builder()
            .id(contract.getContractUuid())
            .contractNumber(contract.getConfirmationNumber())
            .customerName(customerName)
            .status(contract.getStatus().name().toLowerCase())
            .createdAt(contract.getCreatedAt())
            .signedAt(contract.getSignedAt())
            .totalAmount(contract.getTotalAmount())
            .build();
    }

    private String getCurrentUserId() {
        return "system";
    }

    @GetMapping("/{contractId}/details")
    public ResponseEntity<ContractDetailsResponse> getContractDetails(@PathVariable String contractId) {
        log.info("Getting contract details for ID: {}", contractId);
        try {
            Optional<Contract> contractOpt = contractService.getContractDetails(contractId);
            if (!contractOpt.isPresent()) {
                return ResponseEntity.notFound().build();
            }
            ContractDetailsResponse response = ContractDetailsResponse.fromContract(contractOpt.get());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting contract details for ID: {}", contractId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{contractId}/history")
    public ResponseEntity<List<ContractChangeHistoryResponse>> getContractHistory(@PathVariable String contractId) {
        log.info("Getting contract history for ID: {}", contractId);
        try {
            List<ContractChangeHistory> history = contractService.getContractHistory(contractId);
            List<ContractChangeHistoryResponse> responses = history.stream()
                .map(ContractChangeHistoryResponse::fromHistory)
                .collect(Collectors.toList());
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            log.error("Error getting contract history for ID: {}", contractId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{contractId}/options")
    public ResponseEntity<List<OptionResponse>> getCurrentOptions(@PathVariable String contractId) {
        log.info("Getting current options for contract ID: {}", contractId);
        try {
            List<Option> options = contractService.getCurrentOptions(contractId);
            List<OptionResponse> responses = options.stream()
                .map(OptionResponse::fromOption)
                .collect(Collectors.toList());
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            log.error("Error getting current options for contract ID: {}", contractId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/{contractId}/plan-change")
    public ResponseEntity<ContractResponse> changePlan(@PathVariable String contractId, @RequestBody PlanChangeRequest request) {
        log.info("Changing plan for contract ID: {} to plan: {}", contractId, request.getNewPlanId());
        try {
            Contract contract = contractService.changePlan(contractId, request.getNewPlanId(), request.getReason(), request.getEffectiveDate());
            ContractResponse response = ContractResponse.fromContract(contract);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error changing plan for contract ID: {}", contractId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/{contractId}/plan-change/simulate")
    public ResponseEntity<FeeCalculationResult> simulatePlanChange(@PathVariable String contractId, @RequestBody PlanChangeSimulationRequest request) {
        log.info("Simulating plan change for contract ID: {} to plan: {}", contractId, request.getNewPlanId());
        try {
            FeeCalculationResult result = contractService.simulatePlanChange(contractId, request.getNewPlanId());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error simulating plan change for contract ID: {}", contractId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{contractId}/available-plans")
    public ResponseEntity<List<PlanResponse>> getAvailablePlans(@PathVariable String contractId) {
        log.info("Getting available plans for contract ID: {}", contractId);
        try {
            List<Plan> plans = contractService.getAvailablePlansForChange(contractId);
            List<PlanResponse> responses = plans.stream()
                .map(PlanResponse::fromPlan)
                .collect(Collectors.toList());
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            log.error("Error getting available plans for contract ID: {}", contractId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/{contractId}/options")
    public ResponseEntity<ContractResponse> addOption(@PathVariable String contractId, @RequestBody OptionRequest request) {
        log.info("Adding option {} to contract ID: {}", request.getOptionId(), contractId);
        try {
            Contract contract = contractService.addOption(contractId, request.getOptionId());
            ContractResponse response = ContractResponse.fromContract(contract);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error adding option to contract ID: {}", contractId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{contractId}/options/{optionId}")
    public ResponseEntity<ContractResponse> removeOption(@PathVariable String contractId, @PathVariable String optionId) {
        log.info("Removing option {} from contract ID: {}", optionId, contractId);
        try {
            Contract contract = contractService.removeOption(contractId, optionId);
            ContractResponse response = ContractResponse.fromContract(contract);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error removing option from contract ID: {}", contractId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{contractId}/options/{optionId}/suspend")
    public ResponseEntity<ContractResponse> suspendOption(@PathVariable String contractId, @PathVariable String optionId) {
        log.info("Suspending option {} for contract ID: {}", optionId, contractId);
        try {
            Contract contract = contractService.suspendOption(contractId, optionId);
            ContractResponse response = ContractResponse.fromContract(contract);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error suspending option for contract ID: {}", contractId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


}
