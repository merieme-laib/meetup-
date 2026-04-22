package com.e11even.backend.config;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class WebConfigTest {

    @Test
    @SuppressWarnings("unchecked")
    void addCorsMappings_shouldRegisterGlobalCorsRules() {
        WebConfig config = new WebConfig();
        CorsRegistry registry = new CorsRegistry();

        config.addCorsMappings(registry);

        List<Object> registrations = (List<Object>) ReflectionTestUtils.getField(registry, "registrations");
        assertFalse(registrations.isEmpty());

        Object registration = registrations.getFirst();
        Object corsConfiguration = ReflectionTestUtils.getField(registration, "config");

        List<String> allowedOrigins = (List<String>) ReflectionTestUtils.invokeMethod(corsConfiguration, "getAllowedOrigins");
        List<String> allowedMethods = (List<String>) ReflectionTestUtils.invokeMethod(corsConfiguration, "getAllowedMethods");
        Boolean allowCredentials = (Boolean) ReflectionTestUtils.invokeMethod(corsConfiguration, "getAllowCredentials");

        assertEquals(List.of("https://192.168.75.120"), allowedOrigins);
        assertEquals(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"), allowedMethods);
        assertEquals(Boolean.TRUE, allowCredentials);
    }
}