package com.ahamo.ekyc.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QualityCheckRequest {
    
    @NotBlank(message = "画像データは必須です")
    private String imageData;
    
    @NotNull(message = "チェックタイプは必須です")
    private CheckType checkType;
    
    private DocumentType documentType;
    
    public enum CheckType {
        DOCUMENT, SELFIE, GENERAL
    }
    
    public enum DocumentType {
        DRIVERS_LICENSE, PASSPORT, RESIDENCE_CARD
    }
}
