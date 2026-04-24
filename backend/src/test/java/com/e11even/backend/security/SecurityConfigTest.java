package com.e11even.backend.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.CorsUtils;

class SecurityConfigTest {

    @Test
    void corsConfigurationSource_shouldExposeExpectedOriginsMethodsAndHeaders() {
        SecurityConfig config = new SecurityConfig();

        CorsConfigurationSource source = config.corsConfigurationSource();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("OPTIONS");
        request.setRequestURI("/api/events");
        request.addHeader("Origin", "http://192.168.75.120:5173");
        request.addHeader("Access-Control-Request-Method", "GET");

        assertTrue(CorsUtils.isPreFlightRequest(request));

        CorsConfiguration cors = source.getCorsConfiguration(request);
        assertEquals(4, cors.getAllowedOrigins().size());
        assertTrue(cors.getAllowedOrigins().contains("http://localhost:5173"));
        assertTrue(cors.getAllowedOrigins().contains("http://192.168.75.120:5173"));
        assertTrue(cors.getAllowedOrigins().contains("http://192.168.75.120"));
        assertTrue(cors.getAllowedOrigins().contains("https://192.168.75.120"));
        assertTrue(cors.getAllowedMethods().contains("GET"));
        assertTrue(cors.getAllowedMethods().contains("POST"));
        assertEquals(1, cors.getAllowedHeaders().size());
        assertEquals("*", cors.getAllowedHeaders().getFirst());
    }
}