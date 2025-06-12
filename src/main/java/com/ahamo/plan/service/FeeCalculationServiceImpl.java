package com.ahamo.plan.service;

import com.ahamo.plan.dto.FeeCalculationRequest;
import com.ahamo.plan.dto.FeeCalculationResult;
import com.ahamo.plan.model.Plan;
import com.ahamo.plan.repository.PlanRepository;
import com.ahamo.option.repository.OptionRepository;
import com.ahamo.option.model.Option;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FeeCalculationServiceImpl implements FeeCalculationService {
    
    private final PlanRepository planRepository;
    private final OptionRepository optionRepository;
    
    private static final BigDecimal TAX_RATE = new BigDecimal("0.10");
    private static final BigDecimal CALL_FEE_PER_MINUTE = new BigDecimal("22.00");
    private static final BigDecimal DATA_FEE_PER_GB = new BigDecimal("550.00");
    private static final BigDecimal SMS_FEE_PER_MESSAGE = new BigDecimal("3.30");
    
    @Override
    public FeeCalculationResult calculateFee(FeeCalculationRequest request) {
        Plan plan = planRepository.findById(request.getPlanId())
                .orElseThrow(() -> new RuntimeException("Plan not found: " + request.getPlanId()));
        
        BigDecimal baseFee = plan.getMonthlyFee();
        
        BigDecimal callFee = request.getCallMinutes().multiply(CALL_FEE_PER_MINUTE);
        BigDecimal dataFee = calculateDataFee(request.getDataUsage(), plan);
        BigDecimal smsFee = request.getSmsCount().multiply(SMS_FEE_PER_MESSAGE);
        
        List<FeeCalculationResult.OptionFee> optionFees = calculateOptionFees(request.getSelectedOptionIds());
        BigDecimal totalOptionFees = optionFees.stream()
                .map(FeeCalculationResult.OptionFee::getFee)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        List<FeeCalculationResult.Discount> discounts = getDefaultDiscounts();
        BigDecimal totalDiscounts = discounts.stream()
                .map(FeeCalculationResult.Discount::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal totalFee = baseFee.add(callFee).add(dataFee).add(smsFee)
                .add(totalOptionFees).subtract(totalDiscounts);
        BigDecimal taxIncluded = totalFee.multiply(BigDecimal.ONE.add(TAX_RATE))
                .setScale(0, RoundingMode.HALF_UP);
        
        FeeCalculationResult.FeeBreakdown breakdown = FeeCalculationResult.FeeBreakdown.builder()
                .baseFee(baseFee)
                .callFee(callFee.add(smsFee))
                .dataFee(dataFee)
                .optionFees(optionFees)
                .discounts(discounts)
                .build();
        
        return FeeCalculationResult.builder()
                .totalFee(totalFee)
                .breakdown(breakdown)
                .taxIncluded(taxIncluded)
                .build();
    }
    
    private BigDecimal calculateDataFee(BigDecimal dataUsage, Plan plan) {
        String dataCapacity = plan.getDataCapacity();
        BigDecimal includedData = extractDataLimit(dataCapacity);
        
        if (dataUsage.compareTo(includedData) > 0) {
            BigDecimal extraData = dataUsage.subtract(includedData);
            return extraData.multiply(DATA_FEE_PER_GB);
        }
        return BigDecimal.ZERO;
    }
    
    private BigDecimal extractDataLimit(String dataCapacity) {
        if (dataCapacity.contains("20GB")) return new BigDecimal("20");
        if (dataCapacity.contains("100GB")) return new BigDecimal("100");
        return new BigDecimal("20");
    }
    
    private List<FeeCalculationResult.OptionFee> getDefaultOptionFees() {
        return new ArrayList<>();
    }
    
    private List<FeeCalculationResult.OptionFee> calculateOptionFees(List<String> selectedOptionIds) {
        if (selectedOptionIds == null || selectedOptionIds.isEmpty()) {
            return new ArrayList<>();
        }
        
        List<Option> selectedOptions = optionRepository.findAllById(selectedOptionIds);
        return selectedOptions.stream()
                .map(option -> FeeCalculationResult.OptionFee.builder()
                        .id(option.getId())
                        .name(option.getName())
                        .fee(option.getMonthlyFee())
                        .build())
                .collect(Collectors.toList());
    }
    
    private List<FeeCalculationResult.Discount> getDefaultDiscounts() {
        return new ArrayList<>();
    }
    
    @Override
    public List<FeeCalculationResult> compareFeePlans(Map<String, Object> usage, List<String> planIds) {
        List<FeeCalculationResult> results = new ArrayList<>();
        
        for (String planId : planIds) {
            FeeCalculationRequest request = new FeeCalculationRequest();
            request.setPlanId(planId);
            request.setDataUsage(new BigDecimal(usage.get("dataUsage").toString()));
            request.setCallMinutes(new BigDecimal(usage.get("callMinutes").toString()));
            request.setSmsCount(new BigDecimal(usage.get("smsCount").toString()));
            
            results.add(calculateFee(request));
        }
        
        return results;
    }
}
