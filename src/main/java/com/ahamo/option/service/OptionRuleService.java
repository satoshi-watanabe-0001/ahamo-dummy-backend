package com.ahamo.option.service;

import com.ahamo.option.dto.CompatibilityResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface OptionRuleService {
    
    boolean isCompatible(String optionId, List<String> selectedOptions);
    
    List<String> getConflicts(String optionId, List<String> selectedOptions);
    
    List<String> getRequiredOptions(String optionId);
    
    CompatibilityResponse checkCompatibility(String optionId, List<String> selectedOptions);
}
