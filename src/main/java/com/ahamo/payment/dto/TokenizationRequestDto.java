package com.ahamo.payment.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenizationRequestDto {
    @NotBlank(message = "暗号化されたカードデータが必要です")
    private String encryptedCardData;
}
