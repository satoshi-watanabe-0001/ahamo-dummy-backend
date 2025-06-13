package com.ahamo.shipping.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import com.ahamo.customer.model.Address;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShippingRequest {

    @NotBlank
    private String orderNumber;

    @NotNull
    private Long contractId;

    @NotNull
    private Long shippingAddressId;

    @NotBlank
    private String deviceId;

    private String deviceColor;

    private String deviceStorage;

    private String deliveryTimeWindow;

    private String deliveryOptions;

    private String providerCode;

    private String addressType;
    
    private String convenienceStoreCode;
    
    private Long deliveryTimeSlotId;
    
    private String deliveryOption;
    
    private String recipientName;
    
    private String recipientPhone;
    
    private String delegationInfo;
    
    private String deliveryNotes;
    
    private String absenceHandling;
    
    private Address alternateAddress;
    
    private Address workAddress;
}
