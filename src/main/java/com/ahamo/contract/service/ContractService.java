package com.ahamo.contract.service;

import com.ahamo.contract.model.Contract;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Optional;

public interface ContractService {

    Optional<Contract> getContractById(String contractId);

    Page<Contract> searchContracts(String customerName, String contractNumber, 
                                 Contract.ContractStatus status, LocalDateTime dateFrom, 
                                 LocalDateTime dateTo, Pageable pageable);

    Contract saveContract(Contract contract);

    void updateContractStatus(String contractId, Contract.ContractStatus status);
}
