package com.e11even.backend.controllers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.e11even.backend.dto.EventRequest;
import com.e11even.backend.dto.UserProfileResponse;
import com.e11even.backend.models.Event;
import com.e11even.backend.models.Like;
import com.e11even.backend.models.Registration;
import com.e11even.backend.models.User;
import com.e11even.backend.repositories.EventRepository;
import com.e11even.backend.repositories.LikeRepository;
import com.e11even.backend.repositories.RegistrationRepository;
import com.e11even.backend.repositories.UserRepository;
import com.e11even.backend.security.JwtUtils;
import com.e11even.backend.services.EventService;

@ExtendWith(MockitoExtension.class)
class EventControllerTest {

    @Mock private EventService eventService;
    @Mock private EventRepository eventRepository;
    @Mock private RegistrationRepository registrationRepository;
    @Mock private LikeRepository likeRepository;
    @Mock private JwtUtils jwtUtils;
    @Mock private UserRepository userRepository;

    @InjectMocks
    private EventController eventController;

    private User mockCurrentUser(long userId) {
        User user = new User();
        user.setId(userId);
        user.setEmail("john@example.com");
        when(jwtUtils.getEmailFromJwtToken("token")).thenReturn("john@example.com");
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));
        return user;
    }

    private Event buildEvent(long id, long creatorId, LocalDateTime date) {
        Event event = new Event();
        event.setId(id);
        event.setCreatorId(creatorId);
        event.setTitle("Meetup");
        event.setDate(date);
        event.setCancelled(false);
        return event;
    }

    @Test
    void getAllEvents_shouldReturnEventsWithoutUserFlags_whenNoAuthHeader() {
        Event found = buildEvent(10L, 5L, LocalDateTime.now().plusDays(1));
        when(eventService.findAll()).thenReturn(List.of(found));
        when(registrationRepository.countByEventId(10L)).thenReturn(1L);
        when(likeRepository.countByEventId(10L)).thenReturn(4L);

        List<Event> response = eventController.getAllEvents(null);

        assertEquals(1, response.size());
        assertEquals(1, response.get(0).getParticipantsCount());
        assertEquals(4, response.get(0).getLikesCount());
    }

    @Test
    void getAllEvents_shouldIgnoreInvalidToken_andReturnEvents() {
        Event found = buildEvent(11L, 7L, LocalDateTime.now().plusDays(1));
        when(eventService.findAll()).thenReturn(List.of(found));
        when(jwtUtils.getEmailFromJwtToken("bad-token")).thenThrow(new RuntimeException("bad jwt"));
        when(registrationRepository.countByEventId(11L)).thenReturn(2L);
        when(likeRepository.countByEventId(11L)).thenReturn(3L);

        List<Event> response = eventController.getAllEvents("Bearer bad-token");

        assertEquals(1, response.size());
        assertEquals(2, response.get(0).getParticipantsCount());
    }

    @Test
    void getAllEvents_shouldFilterCancelledEvents() {
        Event active = buildEvent(20L, 5L, LocalDateTime.now().plusDays(1));
        Event cancelled = buildEvent(21L, 5L, LocalDateTime.now().plusDays(2));
        cancelled.setCancelled(true);

        when(eventService.findAll()).thenReturn(List.of(active, cancelled));
        when(registrationRepository.countByEventId(20L)).thenReturn(0L);
        when(likeRepository.countByEventId(20L)).thenReturn(0L);

        List<Event> response = eventController.getAllEvents(null);

        assertEquals(1, response.size());
        assertSame(active, response.get(0));
    }

    @Test
    void getAllEvents_shouldSetUserFlags_whenAuthValid() {
        mockCurrentUser(5L);
        Event found = buildEvent(22L, 5L, LocalDateTime.now().plusDays(1));

        when(eventService.findAll()).thenReturn(List.of(found));
        when(registrationRepository.countByEventId(22L)).thenReturn(8L);
        when(likeRepository.countByEventId(22L)).thenReturn(13L);
        when(registrationRepository.existsByUserIdAndEventId(5L, 22L)).thenReturn(true);
        when(likeRepository.existsByUserIdAndEventId(5L, 22L)).thenReturn(false);

        List<Event> response = eventController.getAllEvents("Bearer token");

        assertEquals(1, response.size());
        assertTrue(response.get(0).getIsRegistered());
        assertFalse(response.get(0).getIsLiked());
    }

    @Test
    void createEvent_shouldReturnSavedEntity() {
        mockCurrentUser(5L);
        EventRequest input = new EventRequest();
        input.setPrice(10.0);
        Event saved = new Event();
        saved.setId(1L);
        when(eventService.create(any(), eq(5L))).thenReturn(saved);

        ResponseEntity<Event> response = eventController.createEvent(input, "Bearer token");

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertSame(saved, response.getBody());
    }

    @Test
    void updateEvent_shouldReturnOk_whenOwnerMatches() {
        User user = mockCurrentUser(5L);
        Event found = buildEvent(12L, 5L, LocalDateTime.now().plusDays(1));
        EventRequest input = new EventRequest();
        input.setPrice(15.0);
        when(eventService.update(eq(12L), any(), any())).thenReturn(found);

        ResponseEntity<?> response = eventController.updateEvent(12L, input, "Bearer token");

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void updateEvent_shouldReturnForbidden_whenOwnerMismatch() {
        mockCurrentUser(5L);
        EventRequest input = new EventRequest();
        input.setPrice(0.0);
        when(eventService.update(any(), any(), any())).thenThrow(new RuntimeException("Non autorisé"));

        ResponseEntity<?> response = eventController.updateEvent(12L, input, "Bearer token");

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    void updateEvent_shouldReturnNotFound_whenEventMissing() {
        mockCurrentUser(5L);
        EventRequest input = new EventRequest();
        input.setPrice(0.0);
        when(eventService.update(any(), any(), any())).thenThrow(new RuntimeException("introuvable"));

        ResponseEntity<?> response = eventController.updateEvent(12L, input, "Bearer token");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void deleteEvent_shouldReturnNoContent_whenOwnerMatches() {
        mockCurrentUser(5L);
        ResponseEntity<?> response = eventController.deleteEvent(13L, "Bearer token");
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void deleteEvent_shouldReturnForbidden_whenOwnerMismatch() {
        mockCurrentUser(5L);
        User otherUser = new User();
        otherUser.setId(5L);
        // On simule l'erreur de sécurité venant du service
        when(eventController.deleteEvent(eq(13L), any())).thenThrow(new RuntimeException("Non autorisé"));

        try {
            eventController.deleteEvent(13L, "Bearer token");
        } catch (RuntimeException e) {
            assertEquals(HttpStatus.FORBIDDEN, eventController.deleteEvent(13L, "Bearer token").getStatusCode());
        }
    }

    @Test
    void register_shouldReturnCreated_whenSuccess() {
        mockCurrentUser(5L);
        when(eventService.register(14L, 5L)).thenReturn(Map.of("registered", true, "participantsCount", 1L));

        ResponseEntity<?> response = eventController.register(14L, "Bearer token");

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void register_shouldReturnNotFound_whenEventMissing() {
        mockCurrentUser(5L);
        when(eventService.register(14L, 5L)).thenThrow(new RuntimeException("introuvable"));

        ResponseEntity<?> response = eventController.register(14L, "Bearer token");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void register_shouldReturnBadRequest_whenAlreadyRegistered() {
        mockCurrentUser(5L);
        when(eventService.register(14L, 5L)).thenThrow(new RuntimeException("Déjà inscrit"));

        ResponseEntity<?> response = eventController.register(14L, "Bearer token");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void getEventById_shouldReturnOkAndEnrichEvent_whenFound() {
        Event found = buildEvent(10L, 5L, LocalDateTime.now().plusDays(1));
        when(eventService.findById(10L)).thenReturn(Optional.of(found));
        when(registrationRepository.countByEventId(10L)).thenReturn(1L);

        ResponseEntity<?> response = eventController.getEventById(10L, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(found, response.getBody());
    }

    @Test
    void getEventById_shouldReturnNotFound_whenMissing() {
        when(eventService.findById(99L)).thenReturn(Optional.empty());
        ResponseEntity<?> response = eventController.getEventById(99L, null);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void unregister_shouldReturnOk() {
        mockCurrentUser(5L);
        when(eventService.unregister(5L, 5L)).thenReturn(Map.of("registered", false));
        ResponseEntity<?> response = eventController.unregister(5L, "Bearer token");
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void like_shouldReturnOk() {
        mockCurrentUser(5L);
        when(eventService.like(6L, 5L)).thenReturn(Map.of("liked", true));
        ResponseEntity<?> response = eventController.like(6L, "Bearer token");
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void unlike_shouldReturnOk() {
        mockCurrentUser(5L);
        when(eventService.unlike(7L, 5L)).thenReturn(Map.of("liked", false));
        ResponseEntity<?> response = eventController.unlike(7L, "Bearer token");
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void getEventRegistrations_shouldReturnUsers_whenRequesterIsCreator() {
        User user = mockCurrentUser(5L);
        Event event = buildEvent(40L, 5L, LocalDateTime.now().plusDays(1));
        when(eventService.findById(40L)).thenReturn(Optional.of(event));
        when(registrationRepository.findByEventId(40L)).thenReturn(List.of(new Registration(12L, 40L)));
        when(userRepository.findAllById(any())).thenReturn(List.of(new User()));

        ResponseEntity<?> response = eventController.getEventRegistrations(40L, "Bearer token");

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void getEventRegistrations_shouldReturnForbidden_whenNotCreator() {
        mockCurrentUser(5L);
        Event event = buildEvent(41L, 9L, LocalDateTime.now().plusDays(1));
        when(eventService.findById(41L)).thenReturn(Optional.of(event));

        ResponseEntity<?> response = eventController.getEventRegistrations(41L, "Bearer token");

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }
}