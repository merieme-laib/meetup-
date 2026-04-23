package com.e11even.backend.services;

import com.e11even.backend.models.User;
import com.e11even.backend.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthService authService;

    @Test
    void register_shouldSaveAndReturnUser() {
        User user = new User();
        user.setEmail("register@example.com");

        when(userRepository.save(user)).thenReturn(user);

        User result = authService.register(user);

        assertSame(user, result);
    }

    @Test
    void login_shouldReturnUser_whenCredentialsAreValid() {
        User user = new User();
        user.setEmail("john@example.com");
        user.setPassword("secret");
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));

        User result = authService.login("john@example.com", "secret");

        assertSame(user, result);
    }

    @Test
    void login_shouldThrow_whenPasswordIsInvalid() {
        User user = new User();
        user.setEmail("john@example.com");
        user.setPassword("good");
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> authService.login("john@example.com", "bad"));

        assertEquals("Email ou mot de passe incorrect", exception.getMessage());
    }

    @Test
    void login_shouldThrow_whenUserNotFound() {
        when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> authService.login("missing@example.com", "any"));

        assertEquals("Email ou mot de passe incorrect", exception.getMessage());
    }
}