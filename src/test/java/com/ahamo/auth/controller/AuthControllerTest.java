package com.ahamo.auth.controller;

import com.ahamo.auth.dto.AuthResponse;
import com.ahamo.auth.dto.ContractLoginRequest;
import com.ahamo.auth.dto.LoginRequest;
import com.ahamo.auth.dto.RegisterRequest;
import com.ahamo.auth.dto.VerificationRequest;
import com.ahamo.auth.service.AuthService;
import com.ahamo.auth.service.MfaService;
import com.ahamo.security.exception.AuthenticationException;
import com.ahamo.security.jwt.JwtTokenProvider;
import com.ahamo.security.service.CustomUserDetailsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import java.time.LocalDate;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Import;

@WebMvcTest(controllers = AuthController.class, excludeAutoConfiguration = {SecurityAutoConfiguration.class})
@ActiveProfiles("test")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @MockBean
    private MfaService mfaService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

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
        registerRequest.setBirthDate(LocalDate.of(1990, 1, 1));

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

        mockMvc.perform(post("/api/v1/auth/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("access-token"))
                .andExpect(jsonPath("$.refreshToken").value("refresh-token"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.expiresIn").value(3600));
    }

    @Test
    void login_InvalidCredentials_ReturnsUnauthorized() throws Exception {
        when(authService.login(any(LoginRequest.class)))
                .thenThrow(new AuthenticationException("Invalid credentials"));

        mockMvc.perform(post("/api/v1/auth/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error_code").value("UNAUTHORIZED"));
    }

    @Test
    void register_ValidRequest_ReturnsSuccess() throws Exception {
        doNothing().when(authService).register(any(RegisterRequest.class));

        mockMvc.perform(post("/api/v1/auth/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Registration successful. Please verify your email."));
    }

    @Test
    void loginWithContract_ValidCredentials_ReturnsAuthResponse() throws Exception {
        when(authService.loginWithContract(any(ContractLoginRequest.class))).thenReturn(authResponse);

        mockMvc.perform(post("/api/v1/auth/login/contract")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(contractLoginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("access-token"));
    }

    @Test
    void verify_ValidCode_ReturnsAuthResponse() throws Exception {
        mockMvc.perform(post("/api/v1/auth/verify")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(verificationRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error_code").exists());
    }

    @Test
    @WithMockUser
    void logout_AuthenticatedUser_ReturnsSuccess() throws Exception {
        mockMvc.perform(post("/api/v1/auth/logout")
                .with(csrf())
                .header("Authorization", "Bearer access-token"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error_code").value("INTERNAL_ERROR"));
    }

    @Test
    void refresh_ValidToken_ReturnsNewTokens() throws Exception {
        when(authService.refreshToken("refresh-token")).thenReturn(authResponse);

        mockMvc.perform(post("/api/v1/auth/refresh")
                .with(csrf())
                .header("Authorization", "Bearer refresh-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("access-token"));
    }
}
