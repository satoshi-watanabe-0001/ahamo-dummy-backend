package com.ahamo.option.service;

import com.ahamo.option.dto.CompatibilityResponse;
import com.ahamo.option.repository.OptionDependencyRepository;
import com.ahamo.option.repository.OptionExclusionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OptionRuleServiceImpl implements OptionRuleService {
    
    private final OptionDependencyRepository dependencyRepository;
    private final OptionExclusionRepository exclusionRepository;
    
    @Override
    public boolean isCompatible(String optionId, List<String> selectedOptions) {
        List<String> exclusions = exclusionRepository.findExcludedOptionIdsByOptionId(optionId);
        for (String selectedOption : selectedOptions) {
            if (exclusions.contains(selectedOption)) {
                return false;
            }
        }
        
        List<String> requiredOptions = dependencyRepository.findRequiredOptionIdsByOptionId(optionId);
        return selectedOptions.containsAll(requiredOptions);
    }
    
    @Override
    public List<String> getConflicts(String optionId, List<String> selectedOptions) {
        List<String> conflicts = new ArrayList<>();
        List<String> exclusions = exclusionRepository.findExcludedOptionIdsByOptionId(optionId);
        
        for (String selectedOption : selectedOptions) {
            if (exclusions.contains(selectedOption)) {
                conflicts.add(selectedOption);
            }
        }
        
        return conflicts;
    }
    
    @Override
    public List<String> getRequiredOptions(String optionId) {
        return dependencyRepository.findRequiredOptionIdsByOptionId(optionId);
    }
    
    @Override
    public CompatibilityResponse checkCompatibility(String optionId, List<String> selectedOptions) {
        List<String> conflicts = getConflicts(optionId, selectedOptions);
        List<String> requiredOptions = getRequiredOptions(optionId);
        List<String> missingRequirements = new ArrayList<>();
        
        for (String required : requiredOptions) {
            if (!selectedOptions.contains(required)) {
                missingRequirements.add(required);
            }
        }
        
        boolean compatible = conflicts.isEmpty() && missingRequirements.isEmpty();
        
        String message;
        if (compatible) {
            message = "オプションは選択されたオプションと互換性があります";
        } else {
            StringBuilder sb = new StringBuilder("互換性の問題があります: ");
            if (!conflicts.isEmpty()) {
                sb.append("競合するオプション: ").append(String.join(", ", conflicts));
            }
            if (!missingRequirements.isEmpty()) {
                if (!conflicts.isEmpty()) sb.append("; ");
                sb.append("必要なオプション: ").append(String.join(", ", missingRequirements));
            }
            message = sb.toString();
        }
        
        return new CompatibilityResponse(compatible, conflicts, missingRequirements, message);
    }
}
