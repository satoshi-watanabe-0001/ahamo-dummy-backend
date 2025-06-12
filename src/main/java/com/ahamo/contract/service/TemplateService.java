package com.ahamo.contract.service;

import com.ahamo.contract.model.ContractTemplate;
import com.ahamo.contract.dto.ContractTemplateCreateRequest;
import com.ahamo.contract.dto.ContractTemplateUpdateRequest;

import java.util.List;
import java.util.Optional;

public interface TemplateService {

    List<ContractTemplate> getAllActiveTemplates();

    Optional<ContractTemplate> getTemplateById(String templateId);

    ContractTemplate createTemplate(ContractTemplateCreateRequest request, String userId);

    ContractTemplate updateTemplate(String templateId, ContractTemplateUpdateRequest request, String userId);

    void deactivateTemplate(String templateId, String userId);

    List<ContractTemplate> searchTemplatesByName(String name);

    List<ContractTemplate> getTemplateVersions(String parentTemplateId);

    Optional<ContractTemplate> getLatestTemplateVersion(String templateName);
}
