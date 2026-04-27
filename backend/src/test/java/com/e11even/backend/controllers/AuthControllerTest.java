package com.e11even.backend.controllers;

import com.e11even.backend.dto.UpdateProfileRequest;
import com.e11even.backend.dto.UserProfileResponse;
import com.e11even.backend.dto.UpdateEmailRequest; 
import com.e11even.backend.dto.UpdatePasswordRequest; 
import com.e11even.backend.models.Event;
import com.e11even.backend.models.Like;
import com.e11even.backend.models.Registration;
import com.e11even.backend.models.User;
import com.e11even.backend.repositories.EventRepository;
import com.e11even.backend.repositories.LikeRepository;
import com.e11even.backend.repositories.RegistrationRepository;
import com.e11even.backend.repositories.UserRepository;
import com.e11even.backend.security.JwtUtils;
import com.e11even.backend.services.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RegistrationRepository registrationRepository;

    @Mock
    private LikeRepository likeRepository;

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private UserController userController;

    private User mockCurrentUser() {
        User user = new User();
        user.setId(7L);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john@example.com");
        user.setPassword("secret");
        when(jwtUtils.getEmailFromJwtToken("token")).thenReturn("john@example.com");
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));
        return user;
    }

    @Test
    void getMe_shouldReturnProfile() {
        User user = mockCurrentUser();

        ResponseEntity<UserProfileResponse> response = userController.getMe("Bearer token");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        UserProfileResponse body = response.getBody();
        assertEquals(user.getEmail(), body.getEmail());
        assertEquals(user.getFirstName(), body.getFirstName());
    }

    @Test
    void updateMe_shouldPersistAndReturnProfile() {
        User user = mockCurrentUser();
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setFirstName("Jane");
        request.setLastName("Smith");
        request.setBio("Bio");
        request.setAvatarUrl("http://example.com/avatar.png");

        User saved = new User();
        saved.setId(user.getId());
        saved.setFirstName("Jane");
        saved.setLastName("Smith");
        saved.setEmail(user.getEmail());
        saved.setBio("Bio");
        saved.setAvatarUrl("http://example.com/avatar.png");
        when(userService.update(user.getId(), user)).thenReturn(saved);

        ResponseEntity<UserProfileResponse> response = userController.updateMe("Bearer token", request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Jane", response.getBody().getFirstName());
        verify(userService).update(user.getId(), user);
    }

    @Test
    void getMyRegistrations_shouldReturnEvents() {
        User user = mockCurrentUser();
        Event event = new Event();
        event.setId(11L);
        event.setTitle("Registered event");
        when(registrationRepository.findByUserId(user.getId())).thenReturn(List.of(new Registration(user.getId(), 11L)));
        when(eventRepository.findAllById(List.of(11L))).thenReturn(List.of(event));

        ResponseEntity<List<Event>> response = userController.getMyRegistrations("Bearer token");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertSame(event, response.getBody().getFirst());
    }

    @Test
    void getMyLikes_shouldReturnEvents() {
        User user = mockCurrentUser();
        Event event = new Event();
        event.setId(12L);
        event.setTitle("Liked event");
        when(likeRepository.findByUserId(user.getId())).thenReturn(List.of(new Like(user.getId(), 12L)));
        when(eventRepository.findAllById(List.of(12L))).thenReturn(List.of(event));

        ResponseEntity<List<Event>> response = userController.getMyLikes("Bearer token");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertSame(event, response.getBody().getFirst());
    }

    @Test
    void updateEmail_shouldReturnTokenAndUser() {
        User user = mockCurrentUser();
        User saved = new User();
        saved.setId(user.getId());
        saved.setEmail("new@example.com");
        when(userService.updateEmail(user.getId(), "new@example.com", "secret")).thenReturn(saved);
        when(jwtUtils.generateJwtToken("new@example.com")).thenReturn("new-token");

        
        UpdateEmailRequest req = new UpdateEmailRequest();
        req.setNewEmail("new@example.com");
        req.setPassword("secret");

        ResponseEntity<?> response = userController.updateEmail("Bearer token", req);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<?, ?> body = assertInstanceOf(Map.class, response.getBody());
        assertEquals("new-token", body.get("token"));
        UserProfileResponse userResponse = assertInstanceOf(UserProfileResponse.class, body.get("user"));
        assertEquals("new@example.com", userResponse.getEmail());
    }

    @Test
    void updateEmail_shouldReturnBadRequest_whenServiceThrows() {
        mockCurrentUser();
        when(userService.updateEmail(7L, "new@example.com", "wrong")).thenThrow(new RuntimeException("Mot de passe incorrect"));

        UpdateEmailRequest req = new UpdateEmailRequest();
        req.setNewEmail("new@example.com");
        req.setPassword("wrong");

        ResponseEntity<?> response = userController.updateEmail("Bearer token", req);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void updatePassword_shouldReturnMessage() {
        User user = mockCurrentUser();
        
        UpdatePasswordRequest req = new UpdatePasswordRequest();
        req.setCurrentPassword("secret");
        req.setNewPassword("new-secret");

        ResponseEntity<?> response = userController.updatePassword("Bearer token", req);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<?, ?> body = assertInstanceOf(Map.class, response.getBody());
        assertEquals("Mot de passe modifié avec succès", body.get("message"));
        verify(userService).updatePassword(user.getId(), "secret", "new-secret");
    }

    @Test
    void updatePassword_shouldReturnBadRequest_whenServiceThrows() {
        mockCurrentUser();
        when(userService.updatePassword(7L, "bad", "new-secret")).thenThrow(new RuntimeException("Mot de passe actuel incorrect"));

        UpdatePasswordRequest req = new UpdatePasswordRequest();
        req.setCurrentPassword("bad");
        req.setNewPassword("new-secret");

        ResponseEntity<?> response = userController.updatePassword("Bearer token", req);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void handleRuntimeException_shouldReturnNotFound() {
        ResponseEntity<?> response = userController.handleRuntimeException(new RuntimeException("absent"));

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        Map<?, ?> body = assertInstanceOf(Map.class, response.getBody());
        assertEquals("absent", body.get("error"));
    }
}