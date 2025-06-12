package com.ahamo.contract.repository;

import com.ahamo.contract.model.ContractTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContractTemplateRepository extends JpaRepository<ContractTemplate, Long> {

    Optional<ContractTemplate> findByTemplateUuid(String templateUuid);

    List<ContractTemplate> findByIsActiveTrue();

    List<ContractTemplate> findByNameContainingIgnoreCase(String name);

    @Query("SELECT t FROM ContractTemplate t WHERE t.name = :name AND t.isActive = true ORDER BY t.version DESC")
    List<ContractTemplate> findActiveTemplatesByName(@Param("name") String name);

    Optional<ContractTemplate> findByNameAndVersionAndIsActiveTrue(String name, String version);

    List<ContractTemplate> findByParentTemplateIdOrderByCreatedAtDesc(String parentTemplateId);
}
