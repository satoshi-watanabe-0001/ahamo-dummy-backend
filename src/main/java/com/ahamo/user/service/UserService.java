package com.ahamo.user.service;

import com.ahamo.auth.dto.RegisterRequest;
import com.ahamo.user.model.User;

import java.util.Optional;

public interface UserService {

    User createUser(RegisterRequest request);
    Optional<User> findByEmail(String email);
    
    Optional<User> findByPhone(String phone);
    
    Optional<User> findByContractNumber(String contractNumber);
    
    Optional<User> findByContractNumberAndBirthDate(String contractNumber, String birthDate);
    
    Optional<User> findById(Long id);
    
    void incrementFailedLoginAttempts(User user);
    
    void resetFailedLoginAttempts(User user);
    
    void verifyEmail(User user);
    
    void verifyPhone(User user);
    
    User createContractUser(String contractNumber, String birthDate, String email, String phone);
    
    boolean isAccountLocked(User user);
}
