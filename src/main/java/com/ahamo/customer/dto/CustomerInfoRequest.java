package com.ahamo.customer.dto;

import com.ahamo.customer.model.Address;
import com.ahamo.customer.model.Customer;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.validation.constraints.Email;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerInfoRequest {
    
    private String firstName;
    private String lastName;
    private String firstNameKana;
    private String lastNameKana;
    private LocalDate birthDate;
    private String gender;
    private String phone;
    
    @Email
    private String email;
    
    private String contractNumber;
    private AddressRequest address;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddressRequest {
        private String postalCode;
        private String prefecture;
        private String city;
        private String addressLine1;
        private String addressLine2;
        private String building;

        public Address toEntity() {
            return Address.builder()
                .postalCode(postalCode)
                .prefecture(prefecture)
                .city(city)
                .addressLine1(addressLine1)
                .addressLine2(addressLine2)
                .building(building)
                .build();
        }
    }

    public Customer toEntity() {
        Customer.CustomerBuilder builder = Customer.builder()
            .firstName(firstName)
            .lastName(lastName)
            .firstNameKana(firstNameKana)
            .lastNameKana(lastNameKana)
            .birthDate(birthDate)
            .phone(phone)
            .email(email)
            .contractNumber(contractNumber);

        if (gender != null) {
            builder.gender(Customer.Gender.valueOf(gender.toUpperCase()));
        }

        if (address != null) {
            builder.address(address.toEntity());
        }

        return builder.build();
    }
}
