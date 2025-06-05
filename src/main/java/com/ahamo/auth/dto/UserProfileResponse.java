package com.ahamo.auth.dto;

import com.ahamo.user.model.User;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class UserProfileResponse {
    private Long id;
    private String email;
    private String name;
    private String phone;
    private LocalDate birthDate;
    private User.Gender gender;
    private String contractNumber;
    private Boolean isEmailVerified;
    private Boolean isPhoneVerified;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static UserProfileResponse fromUser(User user) {
        String fullName = null;
        if (user.getFirstName() != null && user.getLastName() != null) {
            fullName = user.getLastName() + " " + user.getFirstName();
        }

        return UserProfileResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(fullName)
                .phone(user.getPhone())
                .birthDate(user.getBirthDate())
                .gender(user.getGender())
                .contractNumber(user.getContractNumber())
                .isEmailVerified(user.getIsEmailVerified())
                .isPhoneVerified(user.getIsPhoneVerified())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
