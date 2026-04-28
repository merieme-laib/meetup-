package com.e11even.backend.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtUtilsTest {

    private final JwtUtils jwtUtils = new JwtUtils();

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtUtils, "jwtSecret", "MaSuperCleSecreteDePlusDe32CaracteresPourLeHmac");
        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", 3600000); // 1 heure
    }

    @Test
    void generateAndReadToken_shouldReturnOriginalEmail() {
        String email = "alice@example.com";
        String token = jwtUtils.generateJwtToken(email);

        assertTrue(jwtUtils.validateJwtToken(token));
        assertEquals(email, jwtUtils.getEmailFromJwtToken(token));
    }

    @Test
    void validateJwtToken_shouldReturnFalse_forInvalidToken() {
        assertFalse(jwtUtils.validateJwtToken("invalid.token.value"));
    }
}