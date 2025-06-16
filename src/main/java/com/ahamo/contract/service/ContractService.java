package com.ahamo.contract.service;

import com.ahamo.contract.model.Contract;
import com.ahamo.contract.model.ContractChangeHistory;
import com.ahamo.plan.dto.FeeCalculationResult;
import com.ahamo.plan.model.Plan;
import com.ahamo.option.model.Option;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ContractService {

    Optional<Contract> getContractById(String contractId);

    Page<Contract> searchContracts(String customerName, String contractNumber, 
                                 Contract.ContractStatus status, LocalDateTime dateFrom, 
                                 LocalDateTime dateTo, Pageable pageable);

    Contract saveContract(Contract contract);

    void updateContractStatus(String contractId, Contract.ContractStatus status);

    Optional<Contract> getContractDetails(String contractId);
    List<ContractChangeHistory> getContractHistory(String contractId);
    
    Contract changePlan(String contractId, String newPlanId, String reason, LocalDateTime effectiveDate);
    FeeCalculationResult simulatePlanChange(String contractId, String newPlanId);
    List<Plan> getAvailablePlansForChange(String contractId);
    
    Contract addOption(String contractId, String optionId);
    Contract removeOption(String contractId, String optionId);
    Contract suspendOption(String contractId, String optionId);
    List<Option> getCurrentOptions(String contractId);
    List<Option> getAvailableOptions(String contractId);
}
