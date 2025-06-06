package com.ahamo.plan.service;

import com.ahamo.plan.dto.FeeCalculationRequest;
import com.ahamo.plan.dto.FeeCalculationResult;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface FeeCalculationService {
    
    FeeCalculationResult calculateFee(FeeCalculationRequest request);
    
    List<FeeCalculationResult> compareFeePlans(Map<String, Object> usage, List<String> planIds);
}
