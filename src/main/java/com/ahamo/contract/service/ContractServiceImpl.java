package com.ahamo.contract.service;

import com.ahamo.contract.model.Contract;
import com.ahamo.contract.repository.ContractRepository;
import com.ahamo.shipping.service.ShippingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ContractServiceImpl implements ContractService {

    private final ContractRepository contractRepository;
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
}
