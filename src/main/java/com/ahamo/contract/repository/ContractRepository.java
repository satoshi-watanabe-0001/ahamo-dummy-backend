package com.ahamo.contract.repository;

import com.ahamo.contract.model.Contract;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ContractRepository extends JpaRepository<Contract, Long> {

    Optional<Contract> findByContractUuid(String contractUuid);

    List<Contract> findByStatus(Contract.ContractStatus status);

    @Query("SELECT c FROM Contract c WHERE " +
           "(:customerName IS NULL OR CONCAT(c.customerFirstName, ' ', c.customerLastName) LIKE %:customerName%) AND " +
           "(:contractNumber IS NULL OR c.confirmationNumber LIKE %:contractNumber%) AND " +
           "(:status IS NULL OR c.status = :status) AND " +
           "(:dateFrom IS NULL OR c.createdAt >= :dateFrom) AND " +
           "(:dateTo IS NULL OR c.createdAt <= :dateTo)")
    Page<Contract> searchContracts(
        @Param("customerName") String customerName,
        @Param("contractNumber") String contractNumber,
        @Param("status") Contract.ContractStatus status,
        @Param("dateFrom") LocalDateTime dateFrom,
        @Param("dateTo") LocalDateTime dateTo,
        Pageable pageable
    );

    List<Contract> findByCustomerEmailAndStatus(String customerEmail, Contract.ContractStatus status);

    List<Contract> findBySignatureStatusAndCreatedAtBefore(Contract.SignatureStatus signatureStatus, LocalDateTime before);

    Optional<Contract> findBySignatureId(String signatureId);
}
