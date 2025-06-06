package com.ahamo.option.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompatibilityResponse {
    
    private boolean compatible;
    private List<String> conflicts;
    private List<String> missingRequirements;
    private String message;
}
