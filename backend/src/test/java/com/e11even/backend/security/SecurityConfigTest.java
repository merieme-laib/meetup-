package com.e11even.backend.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.util.ReflectionTestUtils;
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

    @Test
    void filterChain_shouldBuildSecurityChain() throws Exception {
        SecurityConfig config = new SecurityConfig();
        ReflectionTestUtils.setField(config, "authTokenFilter", new AuthTokenFilter());

        HttpSecurity http = mock(HttpSecurity.class);
        DefaultSecurityFilterChain chain = mock(DefaultSecurityFilterChain.class);

        when(http.cors(any())).thenReturn(http);
        when(http.csrf(any())).thenReturn(http);
        when(http.sessionManagement(any())).thenReturn(http);
        when(http.authorizeHttpRequests(any())).thenReturn(http);
        when(http.addFilterBefore(any(), any())).thenReturn(http);
        when(http.build()).thenReturn(chain);

        SecurityFilterChain result = config.filterChain(http);

        assertTrue(result != null);
    }
}
