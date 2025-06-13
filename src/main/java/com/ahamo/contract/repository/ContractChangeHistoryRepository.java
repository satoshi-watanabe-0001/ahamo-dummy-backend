package com.ahamo.contract.repository;

import com.ahamo.contract.model.ContractChangeHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContractChangeHistoryRepository extends JpaRepository<ContractChangeHistory, Long> {
    
    List<ContractChangeHistory> findByContractUuidOrderByCreatedAtDesc(String contractUuid);
    
    Page<ContractChangeHistory> findByContractUuidOrderByCreatedAtDesc(String contractUuid, Pageable pageable);
    
    List<ContractChangeHistory> findByContractUuidAndChangeTypeOrderByCreatedAtDesc(String contractUuid, ContractChangeHistory.ChangeType changeType);
}
