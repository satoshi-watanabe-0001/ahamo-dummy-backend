package com.ahamo.pricing.service;

import com.ahamo.pricing.dto.PricingCalculationRequest;
import com.ahamo.pricing.dto.PricingCalculationResult;
import com.ahamo.pricing.dto.EstimateRequest;
import com.ahamo.pricing.dto.EstimateResult;

public interface PricingService {
    
    PricingCalculationResult calculatePricing(PricingCalculationRequest request);
    
    EstimateResult generateEstimate(EstimateRequest request);
}
