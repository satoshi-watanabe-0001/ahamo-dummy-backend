package com.ahamo.auth.controller;

import com.ahamo.auth.dto.*;
import com.ahamo.auth.service.AuthService;
import com.ahamo.auth.service.MfaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;
    private final MfaService mfaService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("Login attempt for email: {}", request.getEmail());
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login/contract")
    public ResponseEntity<AuthResponse> loginWithContract(@Valid @RequestBody ContractLoginRequest request) {
        log.info("Contract login attempt for contract number: {}", request.getContractNumber());
        AuthResponse response = authService.loginWithContract(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@Valid @RequestBody RegisterRequest request) {
        log.info("Registration attempt for email: {}", request.getEmail());
        
        authService.register(request);
        
        String verificationCode = mfaService.generateAndSendEmailVerification(request.getEmail());
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Registration successful. Please verify your email.");
        response.put("email", request.getEmail());
        response.put("verification_required", true);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/verify")
    public ResponseEntity<Map<String, Object>> verify(@Valid @RequestBody VerificationRequest request) {
        log.info("Verification attempt for type: {}", request.getType());
        
        boolean isValid = false;
        
        if ("email".equals(request.getType())) {
            isValid = mfaService.verifyEmailCode(request.getEmail(), request.getCode());
        } else if ("phone".equals(request.getType())) {
            isValid = mfaService.verifySmsCode(request.getPhone(), request.getCode());
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("verified", isValid);
        response.put("message", isValid ? "Verification successful" : "Invalid verification code");
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestHeader("Authorization") String refreshToken) {
        log.info("Token refresh attempt");
        
        String token = refreshToken.replace("Bearer ", "");
        AuthResponse response = authService.refreshToken(token);
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(Authentication authentication) {
        log.info("Logout attempt for user: {}", authentication.getName());
        
        authService.logout(authentication.getName());
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Logout successful");
        
        return ResponseEntity.ok(response);
    }
}
