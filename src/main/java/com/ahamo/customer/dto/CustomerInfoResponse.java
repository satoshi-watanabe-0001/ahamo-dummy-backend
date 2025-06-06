package com.ahamo.customer.dto;

import com.ahamo.customer.model.Address;
import com.ahamo.customer.model.Customer;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerInfoResponse {
    
    private Long id;
    private String firstName;
    private String lastName;
    private String firstNameKana;
    private String lastNameKana;
    private LocalDate birthDate;
    private String gender;
    private String phone;
    private String email;
    private String contractNumber;
    private AddressResponse address;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddressResponse {
        private String postalCode;
        private String prefecture;
        private String city;
        private String addressLine1;
        private String addressLine2;
        private String building;

        public static AddressResponse fromEntity(Address address) {
            if (address == null) {
                return null;
            }
            
            return AddressResponse.builder()
                .postalCode(address.getPostalCode())
                .prefecture(address.getPrefecture())
                .city(address.getCity())
                .addressLine1(address.getAddressLine1())
                .addressLine2(address.getAddressLine2())
                .building(address.getBuilding())
                .build();
        }
    }

    public static CustomerInfoResponse fromEntity(Customer customer) {
        return CustomerInfoResponse.builder()
            .id(customer.getId())
            .firstName(customer.getFirstName())
            .lastName(customer.getLastName())
            .firstNameKana(customer.getFirstNameKana())
            .lastNameKana(customer.getLastNameKana())
            .birthDate(customer.getBirthDate())
            .gender(customer.getGender() != null ? customer.getGender().name().toLowerCase() : null)
            .phone(customer.getPhone())
            .email(customer.getEmail())
            .contractNumber(customer.getContractNumber())
            .address(AddressResponse.fromEntity(customer.getAddress()))
            .createdAt(customer.getCreatedAt())
            .updatedAt(customer.getUpdatedAt())
            .build();
    }
}
