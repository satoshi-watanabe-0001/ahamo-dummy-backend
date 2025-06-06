package com.ahamo.option.service;

import com.ahamo.option.dto.OptionRequest;
import com.ahamo.option.dto.OptionResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface OptionService {
    
    OptionResponse createOption(OptionRequest request);
    
    OptionResponse updateOption(String optionId, OptionRequest request);
    
    void deleteOption(String optionId);
    
    List<OptionResponse> getAllOptions();
    
    List<OptionResponse> getActiveOptions();
    
    List<OptionResponse> getOptionsByCategory(String category);
}
