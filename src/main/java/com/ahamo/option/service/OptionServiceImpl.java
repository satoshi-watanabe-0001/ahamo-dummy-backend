package com.ahamo.option.service;

import com.ahamo.option.dto.OptionRequest;
import com.ahamo.option.dto.OptionResponse;
import com.ahamo.option.model.Option;
import com.ahamo.option.model.OptionDependency;
import com.ahamo.option.model.OptionExclusion;
import com.ahamo.option.repository.OptionRepository;
import com.ahamo.option.repository.OptionDependencyRepository;
import com.ahamo.option.repository.OptionExclusionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OptionServiceImpl implements OptionService {
    
    private final OptionRepository optionRepository;
    private final OptionDependencyRepository dependencyRepository;
    private final OptionExclusionRepository exclusionRepository;
    
    @Override
    @Transactional
    public OptionResponse createOption(OptionRequest request) {
        Option option = new Option();
        option.setId("option_" + UUID.randomUUID().toString().replace("-", "").substring(0, 8));
        option.setName(request.getName());
        option.setCategory(Option.OptionCategory.valueOf(request.getCategory()));
        option.setDescription(request.getDescription());
        option.setMonthlyFee(request.getMonthlyFee());
        option.setOneTimeFee(request.getOneTimeFee() != null ? request.getOneTimeFee() : BigDecimal.ZERO);
        option.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);
        option.setEffectiveStartDate(request.getEffectiveStartDate());
        option.setEffectiveEndDate(request.getEffectiveEndDate());
        option.setCreatedAt(LocalDateTime.now());
        option.setUpdatedAt(LocalDateTime.now());
        option.setCreatedBy("admin@example.com");
        option.setUpdatedBy("admin@example.com");
        
        Option savedOption = optionRepository.save(option);
        
        if (request.getRequiredOptions() != null) {
            for (String requiredOptionId : request.getRequiredOptions()) {
                OptionDependency dependency = new OptionDependency();
                dependency.setOptionId(savedOption.getId());
                dependency.setRequiredOptionId(requiredOptionId);
                dependencyRepository.save(dependency);
            }
        }
        
        if (request.getExcludedOptions() != null) {
            for (String excludedOptionId : request.getExcludedOptions()) {
                OptionExclusion exclusion = new OptionExclusion();
                exclusion.setOptionId(savedOption.getId());
                exclusion.setExcludedOptionId(excludedOptionId);
                exclusionRepository.save(exclusion);
            }
        }
        
        return convertToResponse(savedOption);
    }
    
    @Override
    @Transactional
    public OptionResponse updateOption(String optionId, OptionRequest request) {
        Option existingOption = optionRepository.findById(optionId)
                .orElseThrow(() -> new RuntimeException("Option not found: " + optionId));
        
        existingOption.setName(request.getName());
        existingOption.setCategory(Option.OptionCategory.valueOf(request.getCategory()));
        existingOption.setDescription(request.getDescription());
        existingOption.setMonthlyFee(request.getMonthlyFee());
        existingOption.setOneTimeFee(request.getOneTimeFee() != null ? request.getOneTimeFee() : BigDecimal.ZERO);
        existingOption.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);
        existingOption.setEffectiveStartDate(request.getEffectiveStartDate());
        existingOption.setEffectiveEndDate(request.getEffectiveEndDate());
        existingOption.setUpdatedAt(LocalDateTime.now());
        existingOption.setUpdatedBy("admin@example.com");
        
        Option savedOption = optionRepository.save(existingOption);
        
        dependencyRepository.deleteByOptionId(optionId);
        exclusionRepository.deleteByOptionId(optionId);
        
        if (request.getRequiredOptions() != null) {
            for (String requiredOptionId : request.getRequiredOptions()) {
                OptionDependency dependency = new OptionDependency();
                dependency.setOptionId(savedOption.getId());
                dependency.setRequiredOptionId(requiredOptionId);
                dependencyRepository.save(dependency);
            }
        }
        
        if (request.getExcludedOptions() != null) {
            for (String excludedOptionId : request.getExcludedOptions()) {
                OptionExclusion exclusion = new OptionExclusion();
                exclusion.setOptionId(savedOption.getId());
                exclusion.setExcludedOptionId(excludedOptionId);
                exclusionRepository.save(exclusion);
            }
        }
        
        return convertToResponse(savedOption);
    }
    
    @Override
    @Transactional
    public void deleteOption(String optionId) {
        Option option = optionRepository.findById(optionId)
                .orElseThrow(() -> new RuntimeException("Option not found: " + optionId));
        
        dependencyRepository.deleteByOptionId(optionId);
        exclusionRepository.deleteByOptionId(optionId);
        optionRepository.delete(option);
    }
    
    @Override
    public List<OptionResponse> getAllOptions() {
        List<Option> options = optionRepository.findAll();
        return options.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<OptionResponse> getActiveOptions() {
        List<Option> options = optionRepository.findByIsActiveTrue();
        return options.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<OptionResponse> getOptionsByCategory(String category) {
        Option.OptionCategory optionCategory = Option.OptionCategory.valueOf(category);
        List<Option> options = optionRepository.findByIsActiveTrueAndCategory(optionCategory);
        return options.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    private OptionResponse convertToResponse(Option option) {
        OptionResponse response = new OptionResponse();
        response.setId(option.getId());
        response.setName(option.getName());
        response.setCategory(option.getCategory().name());
        response.setDescription(option.getDescription());
        response.setMonthlyFee(option.getMonthlyFee());
        response.setOneTimeFee(option.getOneTimeFee());
        response.setIsActive(option.getIsActive());
        response.setEffectiveStartDate(option.getEffectiveStartDate());
        response.setEffectiveEndDate(option.getEffectiveEndDate());
        response.setCreatedBy(option.getCreatedBy());
        response.setUpdatedBy(option.getUpdatedBy());
        response.setCreatedAt(option.getCreatedAt());
        response.setUpdatedAt(option.getUpdatedAt());
        
        List<String> requiredOptions = dependencyRepository.findRequiredOptionIdsByOptionId(option.getId());
        response.setRequiredOptions(requiredOptions);
        
        List<String> excludedOptions = exclusionRepository.findExcludedOptionIdsByOptionId(option.getId());
        response.setExcludedOptions(excludedOptions);
        
        return response;
    }
}
