package com.e11even.backend.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtUtilsTest {

    private final JwtUtils jwtUtils = new JwtUtils();

    @Test
    void generateAndReadToken_shouldReturnOriginalEmail() {
        String token = jwtUtils.generateJwtToken("alice@example.com");

        assertTrue(jwtUtils.validateJwtToken(token));
        assertEquals("alice@example.com", jwtUtils.getEmailFromJwtToken(token));
    }

    @Test
    void validateJwtToken_shouldReturnFalse_forInvalidToken() {
        assertFalse(jwtUtils.validateJwtToken("invalid.token.value"));
    }
}