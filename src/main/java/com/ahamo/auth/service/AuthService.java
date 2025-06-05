package com.ahamo.auth.service;

import com.ahamo.auth.dto.*;
import com.ahamo.security.exception.AuthenticationException;
import com.ahamo.security.jwt.JwtTokenProvider;
import com.ahamo.session.service.SessionService;
import com.ahamo.user.model.User;
import com.ahamo.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtTokenProvider tokenProvider;
    private final SessionService sessionService;
    private final PasswordEncoder passwordEncoder;

    public AuthResponse login(LoginRequest request) {
        Optional<User> userOpt = userService.findByEmail(request.getEmail());
        
        if (userOpt.isEmpty()) {
            throw new AuthenticationException("Invalid credentials");
        }
        
        User user = userOpt.get();
        
        if (userService.isAccountLocked(user)) {
            throw new AuthenticationException("Account is temporarily locked");
        }
        
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            userService.incrementFailedLoginAttempts(user);
            throw new AuthenticationException("Invalid credentials");
        }
        
        userService.resetFailedLoginAttempts(user);
        
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        String accessToken = tokenProvider.generateAccessToken(authentication);
        String refreshToken = tokenProvider.generateRefreshToken(request.getEmail());
        
        sessionService.createSession(user.getId().toString(), accessToken);
        
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(3600L)
                .build();
    }

    public AuthResponse loginWithContract(ContractLoginRequest request) {
        Optional<User> userOpt = userService.findByContractNumberAndBirthDate(
                request.getContractNumber(), request.getBirthDate());
        
        if (userOpt.isEmpty()) {
            throw new AuthenticationException("Invalid contract credentials");
        }
        
        User user = userOpt.get();
        
        if (userService.isAccountLocked(user)) {
            throw new AuthenticationException("Account is temporarily locked");
        }
        
        userService.resetFailedLoginAttempts(user);
        
        String accessToken = tokenProvider.generateAccessToken(user.getEmail());
        String refreshToken = tokenProvider.generateRefreshToken(user.getEmail());
        
        sessionService.createSession(user.getId().toString(), accessToken);
        
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(3600L)
                .build();
    }

    public void register(RegisterRequest request) {
        userService.createUser(request);
    }

    public AuthResponse refreshToken(String refreshToken) {
        if (!tokenProvider.validateToken(refreshToken) || !tokenProvider.isRefreshToken(refreshToken)) {
            throw new AuthenticationException("Invalid refresh token");
        }
        
        String username = tokenProvider.getUsernameFromToken(refreshToken);
        String newAccessToken = tokenProvider.generateAccessToken(username);
        String newRefreshToken = tokenProvider.generateRefreshToken(username);
        
        Optional<User> userOpt = userService.findByEmail(username);
        if (userOpt.isPresent()) {
            sessionService.createSession(userOpt.get().getId().toString(), newAccessToken);
        }
        
        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .tokenType("Bearer")
                .expiresIn(3600L)
                .build();
    }

    public void logout(String username) {
        Optional<User> userOpt = userService.findByEmail(username);
        if (userOpt.isPresent()) {
            sessionService.invalidateSession(userOpt.get().getId().toString());
        }
    }
}
