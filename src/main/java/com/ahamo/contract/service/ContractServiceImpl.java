package com.ahamo.contract.service;

import com.ahamo.contract.model.Contract;
import com.ahamo.contract.model.ContractChangeHistory;
import com.ahamo.contract.repository.ContractRepository;
import com.ahamo.contract.repository.ContractChangeHistoryRepository;
import com.ahamo.plan.dto.FeeCalculationRequest;
import com.ahamo.plan.dto.FeeCalculationResult;
import com.ahamo.plan.model.Plan;
import com.ahamo.option.model.Option;
import com.ahamo.plan.repository.PlanRepository;
import com.ahamo.option.repository.OptionRepository;
import com.ahamo.plan.service.FeeCalculationService;
import com.ahamo.shipping.service.ShippingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ContractServiceImpl implements ContractService {

    private final ContractRepository contractRepository;
    private final ContractChangeHistoryRepository changeHistoryRepository;
    private final PlanRepository planRepository;
    private final OptionRepository optionRepository;
    private final FeeCalculationService feeCalculationService;
    private final ShippingService shippingService;


    @Override
    @Transactional(readOnly = true)
    public Optional<Contract> getContractById(String contractId) {
        log.info("Getting contract by ID: {}", contractId);
        return contractRepository.findByContractUuid(contractId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Contract> searchContracts(String customerName, String contractNumber, 
                                        Contract.ContractStatus status, LocalDateTime dateFrom, 
                                        LocalDateTime dateTo, Pageable pageable) {
        log.info("Searching contracts with filters");
        return contractRepository.searchContracts(customerName, contractNumber, status, 
                                                dateFrom, dateTo, pageable);
    }

    @Override
    public Contract saveContract(Contract contract) {
        log.info("Saving contract: {}", contract.getContractUuid());
        return contractRepository.save(contract);
    }

    @Override
    public void updateContractStatus(String contractId, Contract.ContractStatus status) {
        log.info("Updating contract status: {} to {}", contractId, status);
        Contract contract = contractRepository.findByContractUuid(contractId)
            .orElseThrow(() -> new IllegalArgumentException("Contract not found: " + contractId));
        
        Contract.ContractStatus previousStatus = contract.getStatus();
        contract.setStatus(status);
        contractRepository.save(contract);
        
        if (status == Contract.ContractStatus.ACTIVE && previousStatus != Contract.ContractStatus.ACTIVE) {
            try {
                log.info("Contract activated, arranging shipping for contract: {}", contractId);
                shippingService.arrangeShippingForContract(contract.getId());
                log.info("Shipping arranged successfully for contract: {}", contractId);
            } catch (Exception e) {
                log.error("Failed to arrange shipping for contract {}: {}", contractId, e.getMessage());
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Contract> getContractDetails(String contractId) {
        log.info("Getting contract details for ID: {}", contractId);
        return contractRepository.findByContractUuid(contractId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ContractChangeHistory> getContractHistory(String contractId) {
        log.info("Getting contract history for ID: {}", contractId);
        return changeHistoryRepository.findByContractUuidOrderByCreatedAtDesc(contractId);
    }

    @Override
    @Transactional
    public Contract changePlan(String contractId, String newPlanId, String reason, LocalDateTime effectiveDate) {
        log.info("Changing plan for contract ID: {} to plan: {}", contractId, newPlanId);
        
        Optional<Contract> contractOpt = contractRepository.findByContractUuid(contractId);
        if (!contractOpt.isPresent()) {
            throw new RuntimeException("Contract not found: " + contractId);
        }
        
        Contract contract = contractOpt.get();
        String oldPlanId = contract.getPlanId();
        
        contract.setPlanId(newPlanId);
        contract.setUpdatedAt(LocalDateTime.now());
        Contract savedContract = contractRepository.save(contract);
        
        ContractChangeHistory history = ContractChangeHistory.builder()
            .contractUuid(contractId)
            .changeType(ContractChangeHistory.ChangeType.PLAN_CHANGE)
            .oldValue(oldPlanId)
            .newValue(newPlanId)
            .reason(reason)
            .effectiveDate(effectiveDate)
            .createdBy("system")
            .build();
        changeHistoryRepository.save(history);
        
        log.info("Plan changed successfully for contract: {}", contractId);
        return savedContract;
    }

    @Override
    @Transactional(readOnly = true)
    public FeeCalculationResult simulatePlanChange(String contractId, String newPlanId) {
        log.info("Simulating plan change for contract ID: {} to plan: {}", contractId, newPlanId);
        
        Optional<Contract> contractOpt = contractRepository.findByContractUuid(contractId);
        if (!contractOpt.isPresent()) {
            throw new RuntimeException("Contract not found: " + contractId);
        }
        
        Contract contract = contractOpt.get();
        
        FeeCalculationRequest request = new FeeCalculationRequest();
        request.setPlanId(newPlanId);
        request.setDataUsage(BigDecimal.valueOf(15.0));
        request.setCallMinutes(BigDecimal.valueOf(120));
        request.setSmsCount(BigDecimal.valueOf(20));
        request.setSelectedOptionIds(contract.getOptions());
        
        return feeCalculationService.calculateFee(request);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Plan> getAvailablePlansForChange(String contractId) {
        log.info("Getting available plans for contract ID: {}", contractId);
        return planRepository.findByIsCurrentVersionTrue();
    }

    @Override
    @Transactional
    public Contract addOption(String contractId, String optionId) {
        log.info("Adding option {} to contract ID: {}", optionId, contractId);
        
        Optional<Contract> contractOpt = contractRepository.findByContractUuid(contractId);
        if (!contractOpt.isPresent()) {
            throw new RuntimeException("Contract not found: " + contractId);
        }
        
        Contract contract = contractOpt.get();
        List<String> options = new ArrayList<>(contract.getOptions());
        if (!options.contains(optionId)) {
            options.add(optionId);
            contract.setOptions(options);
            contract.setUpdatedAt(LocalDateTime.now());
            Contract savedContract = contractRepository.save(contract);
            
            ContractChangeHistory history = ContractChangeHistory.builder()
                .contractUuid(contractId)
                .changeType(ContractChangeHistory.ChangeType.OPTION_ADD)
                .newValue(optionId)
                .createdBy("system")
                .build();
            changeHistoryRepository.save(history);
            
            return savedContract;
        }
        
        return contract;
    }

    @Override
    @Transactional
    public Contract removeOption(String contractId, String optionId) {
        log.info("Removing option {} from contract ID: {}", optionId, contractId);
        
        Optional<Contract> contractOpt = contractRepository.findByContractUuid(contractId);
        if (!contractOpt.isPresent()) {
            throw new RuntimeException("Contract not found: " + contractId);
        }
        
        Contract contract = contractOpt.get();
        List<String> options = new ArrayList<>(contract.getOptions());
        if (options.remove(optionId)) {
            contract.setOptions(options);
            contract.setUpdatedAt(LocalDateTime.now());
            Contract savedContract = contractRepository.save(contract);
            
            ContractChangeHistory history = ContractChangeHistory.builder()
                .contractUuid(contractId)
                .changeType(ContractChangeHistory.ChangeType.OPTION_REMOVE)
                .oldValue(optionId)
                .createdBy("system")
                .build();
            changeHistoryRepository.save(history);
            
            return savedContract;
        }
        
        return contract;
    }

    @Override
    @Transactional
    public Contract suspendOption(String contractId, String optionId) {
        log.info("Suspending option {} for contract ID: {}", optionId, contractId);
        
        Optional<Contract> contractOpt = contractRepository.findByContractUuid(contractId);
        if (!contractOpt.isPresent()) {
            throw new RuntimeException("Contract not found: " + contractId);
        }
        
        Contract contract = contractOpt.get();
        
        ContractChangeHistory history = ContractChangeHistory.builder()
            .contractUuid(contractId)
            .changeType(ContractChangeHistory.ChangeType.OPTION_SUSPEND)
            .oldValue(optionId)
            .reason("オプション一時停止")
            .createdBy("system")
            .build();
        changeHistoryRepository.save(history);
        
        return contract;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Option> getCurrentOptions(String contractId) {
        log.info("Getting current options for contract ID: {}", contractId);
        
        Optional<Contract> contractOpt = contractRepository.findByContractUuid(contractId);
        if (!contractOpt.isPresent()) {
            return new ArrayList<>();
        }
        
        Contract contract = contractOpt.get();
        return contract.getOptions().stream()
            .map(optionId -> optionRepository.findById(optionId))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Option> getAvailableOptions(String contractId) {
        log.info("Getting available options for contract ID: {}", contractId);
        return optionRepository.findByIsActiveTrue();
    }
}
