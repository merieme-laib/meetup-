package com.e11even.backend.controllers;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.e11even.backend.dto.RegisterRequest;
import com.e11even.backend.dto.UserProfileResponse;
import com.e11even.backend.models.User;
import com.e11even.backend.security.JwtUtils;
import com.e11even.backend.services.AuthService;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @Mock
    private JwtUtils jwtUtils;

    @InjectMocks
    private AuthController authController;

    @Test
    void register_shouldReturnTokenAndUser_whenSuccess() {
        RegisterRequest input = new RegisterRequest();
        input.setFirstName("John");
        input.setLastName("Doe");
        input.setEmail("john@example.com");
        input.setPassword("secret");

        User saved = new User();
        saved.setEmail("john@example.com");

        when(authService.register(org.mockito.ArgumentMatchers.any(User.class))).thenReturn(saved);
        when(jwtUtils.generateJwtToken("john@example.com")).thenReturn("jwt-token");

        ResponseEntity<?> response = authController.register(input);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<?, ?> body = assertInstanceOf(Map.class, response.getBody());
        assertEquals("jwt-token", body.get("token"));
        UserProfileResponse userProfile = assertInstanceOf(UserProfileResponse.class, body.get("user"));
        assertEquals("john@example.com", userProfile.getEmail());
    }

    @Test
    void register_shouldReturnBadRequest_whenServiceThrows() {
        RegisterRequest input = new RegisterRequest();
        input.setEmail("john@example.com");
        input.setPassword("secret");
        when(authService.register(org.mockito.ArgumentMatchers.any(User.class))).thenThrow(new RuntimeException("already exists"));

        ResponseEntity<?> response = authController.register(input);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void login_shouldReturnTokenAndUser_whenSuccess() {
        User loginRequest = new User();
        loginRequest.setEmail("john@example.com");
        loginRequest.setPassword("secret");

        User found = new User();
        found.setEmail("john@example.com");

        when(authService.login("john@example.com", "secret")).thenReturn(found);
        when(jwtUtils.generateJwtToken("john@example.com")).thenReturn("jwt-token");

        ResponseEntity<?> response = authController.login(loginRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<?, ?> body = assertInstanceOf(Map.class, response.getBody());
        assertEquals("jwt-token", body.get("token"));
        UserProfileResponse userProfile = assertInstanceOf(UserProfileResponse.class, body.get("user"));
        assertEquals("john@example.com", userProfile.getEmail());
    }

    @Test
    void login_shouldReturnUnauthorized_whenServiceReturnsNull() {
        User loginRequest = new User();
        loginRequest.setEmail("john@example.com");
        loginRequest.setPassword("secret");

        when(authService.login("john@example.com", "secret")).thenReturn(null);

        ResponseEntity<?> response = authController.login(loginRequest);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void login_shouldReturnUnauthorized_whenServiceThrows() {
        User loginRequest = new User();
        loginRequest.setEmail("john@example.com");
        loginRequest.setPassword("secret");

        when(authService.login("john@example.com", "secret")).thenThrow(new RuntimeException("bad credentials"));

        ResponseEntity<?> response = authController.login(loginRequest);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void login_shouldReturnBadRequest_whenFieldsAreMissing() {
        User loginRequest = new User();
        loginRequest.setEmail("john@example.com");
        loginRequest.setPassword(" ");

        ResponseEntity<?> response = authController.login(loginRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<?, ?> body = assertInstanceOf(Map.class, response.getBody());
        assertEquals("Email et mot de passe sont obligatoires", body.get("error"));
    }
}