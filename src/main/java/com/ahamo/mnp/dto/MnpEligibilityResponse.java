package com.ahamo.mnp.dto;

import lombok.Data;
import lombok.Builder;

import java.util.List;

@Data
@Builder
public class MnpEligibilityResponse {
    
    private Boolean eligible;
    private String phoneNumber;
    private String currentCarrier;
    private String estimatedPortingTime;
    private List<String> additionalRequirements;
    private List<String> restrictions;
}
