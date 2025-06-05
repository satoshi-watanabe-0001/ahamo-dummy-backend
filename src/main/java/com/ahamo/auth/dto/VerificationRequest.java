package com.ahamo.auth.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class VerificationRequest {

    @NotBlank(message = "Verification code is required")
    private String code;

    @NotBlank(message = "Verification type is required")
    private String type;

    private String email;
    private String phone;
}
