package com.ahamo.monitoring.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlertRequest {
    
    @NotBlank(message = "アラートタイプは必須です")
    private String alertType;
    
    @NotBlank(message = "重要度は必須です")
    private String severity;
    
    @NotBlank(message = "タイトルは必須です")
    private String title;
    
    private String description;
    
    private String source;
    
    private Map<String, Object> metadata;
}
