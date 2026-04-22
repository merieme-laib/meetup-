package com.e11even.backend;

import com.e11even.backend.models.User;
import com.e11even.backend.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HelloControllerTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private HelloController helloController;

    @Test
    void sayHello_shouldCreateUser_whenEmailNotPresent() {
        User another = new User();
        another.setEmail("other@example.com");
        when(userRepository.findAll()).thenReturn(List.of(another));

        String result = helloController.sayHello();

        verify(userRepository).save(any(User.class));
        assertTrue(result.contains("Backend Connecté"));
        assertTrue(result.contains("(Ajouté !)"));
    }

    @Test
    void sayHello_shouldNotCreateUser_whenEmailAlreadyPresent() {
        User existing = new User();
        existing.setEmail("oxcar@example.com");
        when(userRepository.findAll()).thenReturn(List.of(existing));

        String result = helloController.sayHello();

        verify(userRepository, never()).save(any(User.class));
        assertTrue(result.contains("(Déjà présent)"));
    }
}