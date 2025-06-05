package com.ahamo.user.service;

import com.ahamo.auth.dto.RegisterRequest;
import com.ahamo.auth.dto.ContractLoginRequest;
import com.ahamo.user.model.User;
import com.ahamo.user.repository.UserRepository;
import com.ahamo.security.exception.AuthenticationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User createUser(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AuthenticationException("Email already exists");
        }

        if (request.getPhone() != null && userRepository.existsByPhone(request.getPhone())) {
            throw new AuthenticationException("Phone number already exists");
        }

        User user = User.builder()
                .email(request.getEmail())
                .phone(request.getPhone())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .firstNameKana(request.getFirstNameKana())
                .lastNameKana(request.getLastNameKana())
                .birthDate(request.getBirthDate())
                .gender(request.getGender())
                .isEmailVerified(false)
                .isPhoneVerified(false)
                .isActive(true)
                .failedLoginAttempts(0)
                .roles(Set.of(User.Role.USER))
                .build();

        return userRepository.save(user);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> findByPhone(String phone) {
        return userRepository.findByPhone(phone);
    }

    public Optional<User> findByContractNumberAndBirthDate(String contractNumber, String birthDate) {
        try {
            return userRepository.findByContractNumberAndBirthDate(contractNumber, 
                java.time.LocalDate.parse(birthDate));
        } catch (Exception e) {
            log.error("Error parsing birth date: {}", birthDate, e);
            return Optional.empty();
        }
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Transactional
    public void incrementFailedLoginAttempts(User user) {
        user.setFailedLoginAttempts(user.getFailedLoginAttempts() + 1);
        if (user.getFailedLoginAttempts() >= 5) {
            user.setLockedUntil(LocalDateTime.now().plusMinutes(15));
        }
        userRepository.save(user);
    }

    @Transactional
    public void resetFailedLoginAttempts(User user) {
        user.setFailedLoginAttempts(0);
        user.setLockedUntil(null);
        userRepository.save(user);
    }

    @Transactional
    public void verifyEmail(User user) {
        user.setIsEmailVerified(true);
        userRepository.save(user);
    }

    @Transactional
    public void verifyPhone(User user) {
        user.setIsPhoneVerified(true);
        userRepository.save(user);
    }

    @Transactional
    public User createContractUser(String contractNumber, String birthDate, String email, String phone) {
        if (userRepository.existsByContractNumber(contractNumber)) {
            throw new AuthenticationException("Contract number already exists");
        }

        String tempPassword = UUID.randomUUID().toString().substring(0, 12);

        User user = User.builder()
                .contractNumber(contractNumber)
                .birthDate(java.time.LocalDate.parse(birthDate))
                .email(email)
                .phone(phone)
                .password(passwordEncoder.encode(tempPassword))
                .isEmailVerified(false)
                .isPhoneVerified(false)
                .isActive(true)
                .failedLoginAttempts(0)
                .roles(Set.of(User.Role.USER))
                .build();

        return userRepository.save(user);
    }

    public boolean isAccountLocked(User user) {
        return user.getLockedUntil() != null && user.getLockedUntil().isAfter(LocalDateTime.now());
    }
}
