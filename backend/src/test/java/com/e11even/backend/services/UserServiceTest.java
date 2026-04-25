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
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void findByEmail_shouldReturnUser() {
        User user = new User();
        user.setEmail("john@example.com");
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));

        User result = userService.findByEmail("john@example.com");

        assertSame(user, result);
    }

    @Test
    void findByEmail_shouldThrow_whenMissing() {
        when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userService.findByEmail("missing@example.com"));

        assertEquals("Utilisateur introuvable", exception.getMessage());
    }

    @Test
    void update_shouldPersistProvidedFields() {
        User existing = new User();
        existing.setId(1L);
        existing.setFirstName("John");
        existing.setLastName("Doe");
        existing.setBio("Old bio");
        existing.setAvatarUrl("old.png");

        User updated = new User();
        updated.setFirstName("Jane");
        updated.setBio("New bio");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(userRepository.save(existing)).thenReturn(existing);

        User result = userService.update(1L, updated);

        assertEquals("Jane", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        assertEquals("New bio", result.getBio());
        assertEquals("old.png", result.getAvatarUrl());
    }

    @Test
    void update_shouldPersistRemainingProvidedFields() {
        User existing = new User();
        existing.setId(1L);
        existing.setFirstName("John");
        existing.setLastName("Doe");
        existing.setBio("Old bio");
        existing.setAvatarUrl("old.png");

        User updated = new User();
        updated.setLastName("Smith");
        updated.setAvatarUrl("new.png");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(userRepository.save(existing)).thenReturn(existing);

        User result = userService.update(1L, updated);

        assertEquals("John", result.getFirstName());
        assertEquals("Smith", result.getLastName());
        assertEquals("Old bio", result.getBio());
        assertEquals("new.png", result.getAvatarUrl());
    }

    @Test
    void updateEmail_shouldChangeEmail() {
        User existing = new User();
        existing.setId(1L);
        existing.setEmail("old@example.com");
        existing.setPassword("secret");

        User other = new User();
        other.setEmail("other@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(userRepository.findByEmail("new@example.com")).thenReturn(Optional.empty());
        when(userRepository.save(existing)).thenReturn(existing);

        User result = userService.updateEmail(1L, "new@example.com", "secret");

        assertEquals("new@example.com", result.getEmail());
    }

    @Test
    void updateEmail_shouldThrow_whenPasswordIsWrong() {
        User existing = new User();
        existing.setId(1L);
        existing.setEmail("old@example.com");
        existing.setPassword("secret");
        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userService.updateEmail(1L, "new@example.com", "wrong"));

        assertEquals("Mot de passe incorrect", exception.getMessage());
    }

    @Test
    void updateEmail_shouldThrow_whenEmailAlreadyUsed() {
        User existing = new User();
        existing.setId(1L);
        existing.setEmail("old@example.com");
        existing.setPassword("secret");
        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(userRepository.findByEmail("new@example.com")).thenReturn(Optional.of(new User()));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userService.updateEmail(1L, "new@example.com", "secret"));

        assertEquals("Email déjà utilisé", exception.getMessage());
    }

    @Test
    void updateEmail_shouldThrow_whenUserMissing() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userService.updateEmail(1L, "new@example.com", "secret"));

        assertEquals("Utilisateur introuvable", exception.getMessage());
    }

    @Test
    void updatePassword_shouldChangePassword() {
        User existing = new User();
        existing.setId(1L);
        existing.setPassword("old");
        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(userRepository.save(existing)).thenReturn(existing);

        User result = userService.updatePassword(1L, "old", "new");

        assertEquals("new", result.getPassword());
    }

    @Test
    void updatePassword_shouldThrow_whenCurrentPasswordIsWrong() {
        User existing = new User();
        existing.setId(1L);
        existing.setPassword("old");
        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userService.updatePassword(1L, "bad", "new"));

        assertEquals("Mot de passe actuel incorrect", exception.getMessage());
    }

    @Test
    void updatePassword_shouldThrow_whenUserMissing() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userService.updatePassword(1L, "old", "new"));

        assertEquals("Utilisateur introuvable", exception.getMessage());
    }

    @Test
    void update_shouldThrow_whenUserMissing() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userService.update(1L, new User()));

        assertEquals("Utilisateur introuvable", exception.getMessage());
    }
}