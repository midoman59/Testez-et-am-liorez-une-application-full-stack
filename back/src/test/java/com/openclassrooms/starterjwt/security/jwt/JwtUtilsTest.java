package com.openclassrooms.starterjwt.security.jwt;

import com.openclassrooms.starterjwt.security.services.UserDetailsImpl;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class JwtUtilsTest {

    private static final String JWT_SECRET = "testSecretKeyForJwtTokenGenerationAndValidationInTestEnvironmentMustBeLongEnoughForHS512MinimumRequirement!!!";

    private JwtUtils jwtUtils;
    private SecretKey secretKey;

    @BeforeEach
    void setUp() {
        jwtUtils = new JwtUtils();
        secretKey = io.jsonwebtoken.security.Keys.hmacShaKeyFor(JWT_SECRET.getBytes(StandardCharsets.UTF_8));
        ReflectionTestUtils.setField(jwtUtils, "jwtSecret", JWT_SECRET);
        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", 60_000);
    }

    @Test
    void generateJwtToken_shouldCreateTokenAndAllowUsernameExtraction() {
        Authentication authentication = mock(Authentication.class);
        UserDetailsImpl principal = UserDetailsImpl.builder()
                .id(1L)
                .username("user@test.com")
                .firstName("John")
                .lastName("Doe")
                .password("secret")
                .build();
        when(authentication.getPrincipal()).thenReturn(principal);

        String token = jwtUtils.generateJwtToken(authentication);

        assertTrue(jwtUtils.validateJwtToken(token));
        assertEquals("user@test.com", jwtUtils.getUserNameFromJwtToken(token));
    }

    @Test
    void validateJwtToken_shouldReturnFalse_forMalformedToken() {
        assertFalse(jwtUtils.validateJwtToken("not-a-jwt"));
    }

    @Test
    void validateJwtToken_shouldReturnFalse_forExpiredToken() {
        String expiredToken = Jwts.builder()
                .subject("expired@test.com")
                .issuedAt(new Date(System.currentTimeMillis() - 120_000))
                .expiration(new Date(System.currentTimeMillis() - 60_000))
                .signWith(secretKey)
                .compact();

        assertFalse(jwtUtils.validateJwtToken(expiredToken));
    }

    @Test
    void validateJwtToken_shouldReturnFalse_forEmptyToken() {
        assertFalse(jwtUtils.validateJwtToken(""));
    }
}
