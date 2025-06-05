package com.ahamo.auth.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ContractLoginRequest {

    @NotBlank(message = "Contract number is required")
    private String contractNumber;

    @NotBlank(message = "Birth date is required")
    private String birthDate;
}
