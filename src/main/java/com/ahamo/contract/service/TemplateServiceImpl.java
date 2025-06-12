package com.ahamo.contract.service;

import com.ahamo.contract.model.ContractTemplate;
import com.ahamo.contract.dto.ContractTemplateCreateRequest;
import com.ahamo.contract.dto.ContractTemplateUpdateRequest;
import com.ahamo.contract.repository.ContractTemplateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TemplateServiceImpl implements TemplateService {

    private final ContractTemplateRepository templateRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ContractTemplate> getAllActiveTemplates() {
        log.info("Fetching all active contract templates");
        return templateRepository.findByIsActiveTrue();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ContractTemplate> getTemplateById(String templateId) {
        log.info("Fetching contract template by ID: {}", templateId);
        return templateRepository.findByTemplateUuid(templateId);
    }

    @Override
    public ContractTemplate createTemplate(ContractTemplateCreateRequest request, String userId) {
        log.info("Creating new contract template: {}", request.getName());
        
        ContractTemplate template = ContractTemplate.builder()
            .templateUuid(UUID.randomUUID().toString())
            .name(request.getName())
            .description(request.getDescription())
            .version("1.0")
            .format(ContractTemplate.TemplateFormat.valueOf(request.getFormat().toUpperCase()))
            .content(request.getContent())
            .isActive(true)
            .createdBy(userId)
            .updatedBy(userId)
            .build();

        ContractTemplate savedTemplate = templateRepository.save(template);
        log.info("Created contract template with ID: {}", savedTemplate.getTemplateUuid());
        
        return savedTemplate;
    }

    @Override
    public ContractTemplate updateTemplate(String templateId, ContractTemplateUpdateRequest request, String userId) {
        log.info("Updating contract template: {}", templateId);
        
        ContractTemplate existingTemplate = templateRepository.findByTemplateUuid(templateId)
            .orElseThrow(() -> new IllegalArgumentException("Template not found: " + templateId));

        String newVersion = generateNextVersion(existingTemplate.getVersion());
        
        ContractTemplate newTemplate = ContractTemplate.builder()
            .templateUuid(UUID.randomUUID().toString())
            .name(request.getName() != null ? request.getName() : existingTemplate.getName())
            .description(request.getDescription() != null ? request.getDescription() : existingTemplate.getDescription())
            .version(newVersion)
            .format(existingTemplate.getFormat())
            .content(request.getContent() != null ? request.getContent() : existingTemplate.getContent())
            .isActive(true)
            .parentTemplateId(existingTemplate.getTemplateUuid())
            .changeReason(request.getChangeReason())
            .createdBy(userId)
            .updatedBy(userId)
            .build();

        existingTemplate.setIsActive(false);
        existingTemplate.setUpdatedBy(userId);
        templateRepository.save(existingTemplate);

        ContractTemplate savedTemplate = templateRepository.save(newTemplate);
        log.info("Updated contract template with new ID: {}", savedTemplate.getTemplateUuid());
        
        return savedTemplate;
    }

    @Override
    public void deactivateTemplate(String templateId, String userId) {
        log.info("Deactivating contract template: {}", templateId);
        
        ContractTemplate template = templateRepository.findByTemplateUuid(templateId)
            .orElseThrow(() -> new IllegalArgumentException("Template not found: " + templateId));

        template.setIsActive(false);
        template.setUpdatedBy(userId);
        templateRepository.save(template);
        
        log.info("Deactivated contract template: {}", templateId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ContractTemplate> searchTemplatesByName(String name) {
        log.info("Searching templates by name: {}", name);
        return templateRepository.findByNameContainingIgnoreCase(name);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ContractTemplate> getTemplateVersions(String parentTemplateId) {
        log.info("Fetching template versions for parent: {}", parentTemplateId);
        return templateRepository.findByParentTemplateIdOrderByCreatedAtDesc(parentTemplateId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ContractTemplate> getLatestTemplateVersion(String templateName) {
        log.info("Fetching latest template version for: {}", templateName);
        List<ContractTemplate> templates = templateRepository.findActiveTemplatesByName(templateName);
        return templates.isEmpty() ? Optional.empty() : Optional.of(templates.get(0));
    }

    private String generateNextVersion(String currentVersion) {
        try {
            String[] parts = currentVersion.split("\\.");
            int major = Integer.parseInt(parts[0]);
            int minor = parts.length > 1 ? Integer.parseInt(parts[1]) : 0;
            return major + "." + (minor + 1);
        } catch (Exception e) {
            log.warn("Failed to parse version {}, using default", currentVersion);
            return "1.1";
        }
    }
}
