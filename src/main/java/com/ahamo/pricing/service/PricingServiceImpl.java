package com.ahamo.pricing.service;

import com.ahamo.pricing.dto.*;
import com.ahamo.plan.model.Plan;
import com.ahamo.plan.repository.PlanRepository;
import com.ahamo.device.model.Device;
import com.ahamo.device.repository.DeviceRepository;
import com.ahamo.option.model.Option;
import com.ahamo.option.repository.OptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PricingServiceImpl implements PricingService {
    
    private final PlanRepository planRepository;
    private final DeviceRepository deviceRepository;
    private final OptionRepository optionRepository;
    
    private static final BigDecimal TAX_RATE = new BigDecimal("0.10");
    private static final BigDecimal NEW_CUSTOMER_DISCOUNT = new BigDecimal("1000.00");
    private static final BigDecimal MNP_TRANSFER_BONUS = new BigDecimal("2000.00");
    private static final BigDecimal TRADE_IN_BASE_VALUE = new BigDecimal("10000.00");
    
    @Override
    public PricingCalculationResult calculatePricing(PricingCalculationRequest request) {
        log.info("Calculating pricing for plan: {}, device: {}", request.getPlanId(), request.getDeviceId());
        
        Plan plan = planRepository.findById(request.getPlanId())
                .orElseThrow(() -> new RuntimeException("Plan not found: " + request.getPlanId()));
        
        Device device = deviceRepository.findById(request.getDeviceId())
                .orElseThrow(() -> new RuntimeException("Device not found: " + request.getDeviceId()));
        
        List<Option> selectedOptions = getSelectedOptions(request.getSelectedOptionIds());
        
        BigDecimal baseFee = plan.getMonthlyFee();
        BigDecimal callFee = calculateCallFee(request.getCallMinutes());
        BigDecimal dataFee = calculateDataFee(request.getDataUsage());
        
        PricingCalculationResult.DeviceCost deviceCost = calculateDeviceCost(device, request);
        List<PricingCalculationResult.OptionFee> optionFees = calculateOptionFees(selectedOptions);
        List<PricingCalculationResult.Discount> discounts = calculateDiscounts(request);
        List<PricingCalculationResult.Campaign> campaigns = calculateCampaigns(plan, request);
        
        BigDecimal totalOptionFees = optionFees.stream()
                .map(PricingCalculationResult.OptionFee::getMonthlyFee)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal totalDiscounts = discounts.stream()
                .map(PricingCalculationResult.Discount::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal totalCampaignDiscounts = campaigns.stream()
                .map(PricingCalculationResult.Campaign::getDiscountAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal monthlyTotal = baseFee.add(callFee).add(dataFee)
                .add(deviceCost.getMonthlyPayment()).add(totalOptionFees)
                .subtract(totalDiscounts).subtract(totalCampaignDiscounts);
        
        BigDecimal initialCost = calculateInitialCost(selectedOptions, deviceCost);
        BigDecimal totalAmount = monthlyTotal.add(initialCost);
        BigDecimal taxIncluded = totalAmount.multiply(BigDecimal.ONE.add(TAX_RATE))
                .setScale(0, RoundingMode.HALF_UP);
        
        PricingCalculationResult.PricingBreakdown breakdown = PricingCalculationResult.PricingBreakdown.builder()
                .baseFee(baseFee)
                .callFee(callFee)
                .dataFee(dataFee)
                .deviceCost(deviceCost)
                .optionFees(optionFees)
                .discounts(discounts)
                .campaigns(campaigns)
                .build();
        
        return PricingCalculationResult.builder()
                .monthlyTotal(monthlyTotal)
                .initialCost(initialCost)
                .totalAmount(totalAmount)
                .taxIncluded(taxIncluded)
                .breakdown(breakdown)
                .build();
    }
    
    @Override
    public EstimateResult generateEstimate(EstimateRequest request) {
        log.info("Generating estimate for plan: {}, device: {}, period: {} months", 
                request.getPlanId(), request.getDeviceId(), request.getEstimatePeriodMonths());
        
        PricingCalculationRequest pricingRequest = PricingCalculationRequest.builder()
                .planId(request.getPlanId())
                .deviceId(request.getDeviceId())
                .deviceColor(request.getDeviceColor())
                .deviceStorage(request.getDeviceStorage())
                .paymentOption(request.getPaymentOption())
                .tradeInDeviceId(request.getTradeInDeviceId())
                .selectedOptionIds(request.getSelectedOptionIds())
                .dataUsage(BigDecimal.ZERO)
                .callMinutes(BigDecimal.ZERO)
                .smsCount(BigDecimal.ZERO)
                .isNewCustomer(request.isNewCustomer())
                .isMnpTransfer(request.isMnpTransfer())
                .build();
        
        PricingCalculationResult pricingResult = calculatePricing(pricingRequest);
        
        Plan plan = planRepository.findById(request.getPlanId())
                .orElseThrow(() -> new RuntimeException("Plan not found: " + request.getPlanId()));
        
        Device device = deviceRepository.findById(request.getDeviceId())
                .orElseThrow(() -> new RuntimeException("Device not found: " + request.getDeviceId()));
        
        List<Option> selectedOptions = getSelectedOptions(request.getSelectedOptionIds());
        
        String estimateId = UUID.randomUUID().toString();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime validUntil = now.plusDays(30);
        
        EstimateResult.EstimateSummary summary = buildEstimateSummary(pricingResult, request.getEstimatePeriodMonths());
        List<EstimateResult.MonthlyBreakdown> monthlyBreakdowns = buildMonthlyBreakdowns(pricingResult, request.getEstimatePeriodMonths());
        EstimateResult.EstimateDetails details = buildEstimateDetails(plan, device, selectedOptions, pricingResult);
        
        return EstimateResult.builder()
                .estimateId(estimateId)
                .createdAt(now)
                .validUntil(validUntil)
                .summary(summary)
                .monthlyBreakdowns(monthlyBreakdowns)
                .details(details)
                .build();
    }
    
    private List<Option> getSelectedOptions(List<String> optionIds) {
        if (optionIds == null || optionIds.isEmpty()) {
            return new ArrayList<>();
        }
        return optionRepository.findAllById(optionIds);
    }
    
    @Cacheable("callFees")
    private BigDecimal calculateCallFee(BigDecimal callMinutes) {
        BigDecimal callRate = new BigDecimal("22.00");
        return callMinutes.multiply(callRate).setScale(2, RoundingMode.HALF_UP);
    }
    
    @Cacheable("dataFees")
    private BigDecimal calculateDataFee(BigDecimal dataUsage) {
        BigDecimal dataRate = new BigDecimal("550.00");
        return dataUsage.multiply(dataRate).setScale(2, RoundingMode.HALF_UP);
    }
    
    private PricingCalculationResult.DeviceCost calculateDeviceCost(Device device, PricingCalculationRequest request) {
        BigDecimal totalPrice = device.getPrice();
        BigDecimal tradeInDiscount = BigDecimal.ZERO;
        
        if (request.getTradeInDeviceId() != null) {
            tradeInDiscount = TRADE_IN_BASE_VALUE;
            totalPrice = totalPrice.subtract(tradeInDiscount);
        }
        
        BigDecimal monthlyPayment;
        String paymentOptionStr;
        
        switch (request.getPaymentOption()) {
            case LUMP_SUM:
                monthlyPayment = BigDecimal.ZERO;
                paymentOptionStr = "一括払い";
                break;
            case INSTALLMENT_24:
                monthlyPayment = totalPrice.divide(new BigDecimal("24"), 2, RoundingMode.HALF_UP);
                paymentOptionStr = "24回分割";
                break;
            case INSTALLMENT_36:
                monthlyPayment = totalPrice.divide(new BigDecimal("36"), 2, RoundingMode.HALF_UP);
                paymentOptionStr = "36回分割";
                break;
            default:
                throw new RuntimeException("Unsupported payment option: " + request.getPaymentOption());
        }
        
        return PricingCalculationResult.DeviceCost.builder()
                .deviceId(device.getId())
                .deviceName(device.getName())
                .totalPrice(totalPrice)
                .monthlyPayment(monthlyPayment)
                .paymentOption(paymentOptionStr)
                .tradeInDiscount(tradeInDiscount)
                .build();
    }
    
    private List<PricingCalculationResult.OptionFee> calculateOptionFees(List<Option> options) {
        return options.stream()
                .map(option -> PricingCalculationResult.OptionFee.builder()
                        .id(option.getId())
                        .name(option.getName())
                        .monthlyFee(option.getMonthlyFee())
                        .oneTimeFee(option.getOneTimeFee() != null ? option.getOneTimeFee() : BigDecimal.ZERO)
                        .build())
                .collect(Collectors.toList());
    }
    
    private List<PricingCalculationResult.Discount> calculateDiscounts(PricingCalculationRequest request) {
        List<PricingCalculationResult.Discount> discounts = new ArrayList<>();
        
        if (request.isNewCustomer()) {
            discounts.add(PricingCalculationResult.Discount.builder()
                    .id("new_customer_discount")
                    .name("新規契約割引")
                    .amount(NEW_CUSTOMER_DISCOUNT)
                    .type("固定額")
                    .build());
        }
        
        if (request.isMnpTransfer()) {
            discounts.add(PricingCalculationResult.Discount.builder()
                    .id("mnp_transfer_bonus")
                    .name("乗り換えボーナス")
                    .amount(MNP_TRANSFER_BONUS)
                    .type("固定額")
                    .build());
        }
        
        return discounts;
    }
    
    private List<PricingCalculationResult.Campaign> calculateCampaigns(Plan plan, PricingCalculationRequest request) {
        List<PricingCalculationResult.Campaign> campaigns = new ArrayList<>();
        
        LocalDateTime now = LocalDateTime.now();
        if (plan.getCampaignStartDate() != null && plan.getCampaignEndDate() != null) {
            if (now.isAfter(plan.getCampaignStartDate()) && now.isBefore(plan.getCampaignEndDate())) {
                campaigns.add(PricingCalculationResult.Campaign.builder()
                        .id("plan_campaign_" + plan.getId())
                        .name("プランキャンペーン")
                        .discountAmount(new BigDecimal("500.00"))
                        .validUntil(plan.getCampaignEndDate().toString())
                        .build());
            }
        }
        
        return campaigns;
    }
    
    private BigDecimal calculateInitialCost(List<Option> options, PricingCalculationResult.DeviceCost deviceCost) {
        BigDecimal optionOneTimeFees = options.stream()
                .map(option -> option.getOneTimeFee() != null ? option.getOneTimeFee() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal deviceInitialCost = BigDecimal.ZERO;
        if ("一括払い".equals(deviceCost.getPaymentOption())) {
            deviceInitialCost = deviceCost.getTotalPrice();
        }
        
        return optionOneTimeFees.add(deviceInitialCost);
    }
    
    private EstimateResult.EstimateSummary buildEstimateSummary(PricingCalculationResult pricingResult, Integer periodMonths) {
        BigDecimal totalForPeriod = pricingResult.getMonthlyTotal().multiply(new BigDecimal(periodMonths));
        BigDecimal totalWithInitial = totalForPeriod.add(pricingResult.getInitialCost());
        BigDecimal totalTax = totalWithInitial.multiply(TAX_RATE).setScale(0, RoundingMode.HALF_UP);
        
        return EstimateResult.EstimateSummary.builder()
                .totalInitialCost(pricingResult.getInitialCost())
                .averageMonthlyFee(pricingResult.getMonthlyTotal())
                .totalAmountForPeriod(totalWithInitial)
                .totalTaxAmount(totalTax)
                .estimatePeriodMonths(periodMonths)
                .build();
    }
    
    private List<EstimateResult.MonthlyBreakdown> buildMonthlyBreakdowns(PricingCalculationResult pricingResult, Integer periodMonths) {
        List<EstimateResult.MonthlyBreakdown> breakdowns = new ArrayList<>();
        
        for (int month = 1; month <= periodMonths; month++) {
            BigDecimal subtotal = pricingResult.getMonthlyTotal();
            BigDecimal tax = subtotal.multiply(TAX_RATE).setScale(0, RoundingMode.HALF_UP);
            BigDecimal total = subtotal.add(tax);
            
            breakdowns.add(EstimateResult.MonthlyBreakdown.builder()
                    .month(month)
                    .baseFee(pricingResult.getBreakdown().getBaseFee())
                    .devicePayment(pricingResult.getBreakdown().getDeviceCost().getMonthlyPayment())
                    .optionFees(pricingResult.getBreakdown().getOptionFees().stream()
                            .map(PricingCalculationResult.OptionFee::getMonthlyFee)
                            .reduce(BigDecimal.ZERO, BigDecimal::add))
                    .discounts(pricingResult.getBreakdown().getDiscounts().stream()
                            .map(PricingCalculationResult.Discount::getAmount)
                            .reduce(BigDecimal.ZERO, BigDecimal::add))
                    .subtotal(subtotal)
                    .tax(tax)
                    .total(total)
                    .build());
        }
        
        return breakdowns;
    }
    
    private EstimateResult.EstimateDetails buildEstimateDetails(Plan plan, Device device, List<Option> options, PricingCalculationResult pricingResult) {
        EstimateResult.PlanDetails planDetails = EstimateResult.PlanDetails.builder()
                .id(plan.getId())
                .name(plan.getName())
                .description(plan.getDescription())
                .monthlyFee(plan.getMonthlyFee())
                .dataCapacity(plan.getDataCapacity())
                .voiceCalls(plan.getVoiceCalls())
                .build();
        
        EstimateResult.DeviceDetails deviceDetails = EstimateResult.DeviceDetails.builder()
                .id(device.getId())
                .name(device.getName())
                .brand(device.getBrand())
                .price(device.getPrice())
                .paymentOption(pricingResult.getBreakdown().getDeviceCost().getPaymentOption())
                .monthlyPayment(pricingResult.getBreakdown().getDeviceCost().getMonthlyPayment())
                .tradeInValue(pricingResult.getBreakdown().getDeviceCost().getTradeInDiscount())
                .build();
        
        List<EstimateResult.OptionDetails> optionDetails = options.stream()
                .map(option -> EstimateResult.OptionDetails.builder()
                        .id(option.getId())
                        .name(option.getName())
                        .category(option.getCategory().toString())
                        .monthlyFee(option.getMonthlyFee())
                        .oneTimeFee(option.getOneTimeFee() != null ? option.getOneTimeFee() : BigDecimal.ZERO)
                        .build())
                .collect(Collectors.toList());
        
        List<EstimateResult.DiscountDetails> discountDetails = pricingResult.getBreakdown().getDiscounts().stream()
                .map(discount -> EstimateResult.DiscountDetails.builder()
                        .id(discount.getId())
                        .name(discount.getName())
                        .amount(discount.getAmount())
                        .type(discount.getType())
                        .description(discount.getName())
                        .build())
                .collect(Collectors.toList());
        
        List<EstimateResult.CampaignDetails> campaignDetails = pricingResult.getBreakdown().getCampaigns().stream()
                .map(campaign -> EstimateResult.CampaignDetails.builder()
                        .id(campaign.getId())
                        .name(campaign.getName())
                        .discountAmount(campaign.getDiscountAmount())
                        .validUntil(campaign.getValidUntil())
                        .description(campaign.getName())
                        .build())
                .collect(Collectors.toList());
        
        return EstimateResult.EstimateDetails.builder()
                .plan(planDetails)
                .device(deviceDetails)
                .options(optionDetails)
                .discounts(discountDetails)
                .campaigns(campaignDetails)
                .build();
    }
}
