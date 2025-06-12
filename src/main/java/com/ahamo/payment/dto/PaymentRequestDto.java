package com.ahamo.payment.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import javax.validation.constraints.NotBlank;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentRequestDto {
    @NotBlank
    private String contractId;
    
    @NotBlank
    private String paymentMethodId;
    
    private Map<String, Object> paymentDetails;
}
