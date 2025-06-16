package com.ahamo.user.service;

import com.ahamo.user.model.User;
import com.ahamo.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public Optional<User> findByPhone(String phone) {
        return userRepository.findByPhone(phone);
    }
    
    @Override
    public Optional<User> findByContractNumber(String contractNumber) {
        return userRepository.findByContractNumber(contractNumber);
    }


    @Override
    public void verifyEmail(User user) {
        user.setIsEmailVerified(true);
        userRepository.save(user);
        log.info("Email verified for user: {}", user.getEmail());
    }

    @Override
    public void verifyPhone(User user) {
        user.setIsPhoneVerified(true);
        userRepository.save(user);
        log.info("Phone verified for user: {}", user.getPhone());
    }

    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public void incrementFailedLoginAttempts(User user) {
        user.setFailedLoginAttempts(user.getFailedLoginAttempts() + 1);
        if (user.getFailedLoginAttempts() >= 5) {
            user.setLockedUntil(java.time.LocalDateTime.now().plusMinutes(15));
        }
        userRepository.save(user);
    }

    @Override
    public void resetFailedLoginAttempts(User user) {
        user.setFailedLoginAttempts(0);
        user.setLockedUntil(null);
        userRepository.save(user);
    }

    @Override
    public User createUser(com.ahamo.auth.dto.RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        if (request.getPhone() != null && userRepository.existsByPhone(request.getPhone())) {
            throw new RuntimeException("Phone number already exists");
        }

        User user = User.builder()
                .email(request.getEmail())
                .phone(request.getPhone())
                .build();

        return userRepository.save(user);
    }

    @Override
    public Optional<User> findByContractNumberAndBirthDate(String contractNumber, String birthDate) {
        try {
            return userRepository.findByContractNumberAndBirthDate(contractNumber, 
                java.time.LocalDate.parse(birthDate));
        } catch (Exception e) {
            log.error("Error parsing birth date: {}", birthDate, e);
            return Optional.empty();
        }
    }

    @Override
    public User createContractUser(String contractNumber, String birthDate, String email, String phone) {
        if (userRepository.existsByContractNumber(contractNumber)) {
            throw new RuntimeException("Contract number already exists");
        }

        String tempPassword = java.util.UUID.randomUUID().toString().substring(0, 12);

        User user = User.builder()
                .contractNumber(contractNumber)
                .birthDate(java.time.LocalDate.parse(birthDate))
                .email(email)
                .phone(phone)
                .build();

        return userRepository.save(user);
    }

    @Override
    public boolean isAccountLocked(User user) {
        return user.getLockedUntil() != null && user.getLockedUntil().isAfter(java.time.LocalDateTime.now());
    }
}
