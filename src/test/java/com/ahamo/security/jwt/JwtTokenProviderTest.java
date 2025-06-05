package com.ahamo.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;
    private final String secretKey = "mySecretKey1234567890123456789012345678901234567890123456789012345678901234567890";
    private final long accessTokenExpiration = 3600000L; // 1 hour
    private final long refreshTokenExpiration = 86400000L; // 24 hours

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider();
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtSecret", secretKey);
        ReflectionTestUtils.setField(jwtTokenProvider, "accessTokenExpiration", accessTokenExpiration);
        ReflectionTestUtils.setField(jwtTokenProvider, "refreshTokenExpiration", refreshTokenExpiration);
        jwtTokenProvider.init();
    }

    @Test
    void generateAccessToken_ValidAuthentication_ReturnsToken() {
        Authentication authentication = createMockAuthentication("test@example.com");

        String token = jwtTokenProvider.generateAccessToken(authentication);

        assertNotNull(token);
        assertTrue(token.length() > 0);
    }

    @Test
    void generateRefreshToken_ValidUsername_ReturnsToken() {
        String token = jwtTokenProvider.generateRefreshToken("testuser");

        assertNotNull(token);
        assertTrue(token.length() > 0);
    }

    @Test
    void getUsernameFromToken_ValidToken_ReturnsUsername() {
        Authentication authentication = createMockAuthentication("test@example.com");
        String token = jwtTokenProvider.generateAccessToken(authentication);

        String username = jwtTokenProvider.getUsernameFromToken(token);

        assertEquals("test@example.com", username);
    }

    @Test
    void isRefreshToken_RefreshToken_ReturnsTrue() {
        String token = jwtTokenProvider.generateRefreshToken("testuser");

        boolean isRefresh = jwtTokenProvider.isRefreshToken(token);

        assertTrue(isRefresh);
    }

    @Test
    void validateToken_ValidToken_ReturnsTrue() {
        Authentication authentication = createMockAuthentication("test@example.com");
        String token = jwtTokenProvider.generateAccessToken(authentication);

        boolean isValid = jwtTokenProvider.validateToken(token);

        assertTrue(isValid);
    }

    @Test
    void validateToken_ExpiredToken_ReturnsFalse() {
        String expiredToken = Jwts.builder()
                .setSubject("test@example.com")
                .setIssuedAt(new Date(System.currentTimeMillis() - 7200000)) // 2 hours ago
                .setExpiration(new Date(System.currentTimeMillis() - 3600000)) // 1 hour ago (expired)
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()))
                .compact();

        boolean isValid = jwtTokenProvider.validateToken(expiredToken);

        assertFalse(isValid);
    }

    @Test
    void validateToken_InvalidToken_ReturnsFalse() {
        String invalidToken = "invalid.token.here";

        boolean isValid = jwtTokenProvider.validateToken(invalidToken);

        assertFalse(isValid);
    }

    @Test
    void getExpirationDateFromToken_ValidToken_ReturnsExpiration() {
        Authentication authentication = createMockAuthentication("test@example.com");
        String token = jwtTokenProvider.generateAccessToken(authentication);

        Date expiration = jwtTokenProvider.getExpirationDateFromToken(token);

        assertNotNull(expiration);
        assertTrue(expiration.after(new Date()));
    }

    private Authentication createMockAuthentication(String username) {
        Authentication authentication = mock(Authentication.class);
        UserDetails userDetails = new User(username, "password", 
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(authentication.getName()).thenReturn(username);
        return authentication;
    }
}
