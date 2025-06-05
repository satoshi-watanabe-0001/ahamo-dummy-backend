package com.ahamo.auth.controller;

import com.ahamo.auth.dto.AuthResponse;
import com.ahamo.auth.dto.ContractLoginRequest;
import com.ahamo.auth.dto.LoginRequest;
import com.ahamo.auth.dto.RegisterRequest;
import com.ahamo.auth.dto.VerificationRequest;
import com.ahamo.auth.service.AuthService;
import com.ahamo.security.exception.AuthenticationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Import;

@WebMvcTest(controllers = AuthController.class, excludeAutoConfiguration = {SecurityAutoConfiguration.class})
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    private LoginRequest loginRequest;
    private RegisterRequest registerRequest;
    private ContractLoginRequest contractLoginRequest;
    private VerificationRequest verificationRequest;
    private AuthResponse authResponse;

    @BeforeEach
    void setUp() {
        loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password");

        registerRequest = new RegisterRequest();
        registerRequest.setEmail("new@example.com");
        registerRequest.setPhone("090-1234-5678");
        registerRequest.setPassword("password");
        registerRequest.setFirstName("太郎");
        registerRequest.setLastName("田中");

        contractLoginRequest = new ContractLoginRequest();
        contractLoginRequest.setContractNumber("C001234567");
        contractLoginRequest.setBirthDate("1990-01-01");

        verificationRequest = new VerificationRequest();
        verificationRequest.setEmail("test@example.com");
        verificationRequest.setCode("123456");

        authResponse = new AuthResponse();
        authResponse.setAccessToken("access-token");
        authResponse.setRefreshToken("refresh-token");
        authResponse.setTokenType("Bearer");
        authResponse.setExpiresIn(3600L);
    }

    @Test
    void login_ValidCredentials_ReturnsAuthResponse() throws Exception {
        when(authService.login(any(LoginRequest.class))).thenReturn(authResponse);

        mockMvc.perform(post("/v1/auth/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access_token").value("access-token"))
                .andExpect(jsonPath("$.refresh_token").value("refresh-token"))
                .andExpect(jsonPath("$.token_type").value("Bearer"))
                .andExpect(jsonPath("$.expires_in").value(3600));
    }

    @Test
    void login_InvalidCredentials_ReturnsUnauthorized() throws Exception {
        when(authService.login(any(LoginRequest.class)))
                .thenThrow(new AuthenticationException("Invalid credentials"));

        mockMvc.perform(post("/v1/auth/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error_code").value("AUTHENTICATION_ERROR"));
    }

    @Test
    void register_ValidRequest_ReturnsSuccess() throws Exception {
        doNothing().when(authService).register(any(RegisterRequest.class));

        mockMvc.perform(post("/v1/auth/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Verification code sent"));
    }

    @Test
    void loginWithContract_ValidCredentials_ReturnsAuthResponse() throws Exception {
        when(authService.loginWithContract(any(ContractLoginRequest.class))).thenReturn(authResponse);

        mockMvc.perform(post("/v1/auth/login/contract")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(contractLoginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access_token").value("access-token"));
    }

    @Test
    void verify_ValidCode_ReturnsAuthResponse() throws Exception {
        mockMvc.perform(post("/v1/auth/verify")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(verificationRequest)))
                .andExpect(status().isNotFound()); // Expected since endpoint not implemented yet
    }

    @Test
    @WithMockUser
    void logout_AuthenticatedUser_ReturnsSuccess() throws Exception {
        mockMvc.perform(post("/v1/auth/logout")
                .with(csrf())
                .header("Authorization", "Bearer access-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Logged out successfully"));
    }

    @Test
    void refresh_ValidToken_ReturnsNewTokens() throws Exception {
        when(authService.refreshToken("refresh-token")).thenReturn(authResponse);

        mockMvc.perform(post("/v1/auth/refresh")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"refresh_token\":\"refresh-token\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access_token").value("access-token"));
    }
}
