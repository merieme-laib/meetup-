package com.e11even.backend.controllers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.e11even.backend.dto.EventRequest;
import com.e11even.backend.dto.UserProfileResponse; // 👈 Import ajouté
import com.e11even.backend.models.Event;
import com.e11even.backend.models.Like;
import com.e11even.backend.models.Registration;
import com.e11even.backend.models.User;
import com.e11even.backend.repositories.EventRepository;
import com.e11even.backend.repositories.LikeRepository;
import com.e11even.backend.repositories.RegistrationRepository;
import com.e11even.backend.repositories.UserRepository;
import com.e11even.backend.security.JwtUtils;

@ExtendWith(MockitoExtension.class)
class EventControllerTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private RegistrationRepository registrationRepository;

    @Mock
    private LikeRepository likeRepository;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private UserRepository userRepository;

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
        return event;
    }

    @Test
    void getAllEvents_shouldReturnEventsWithoutUserFlags_whenNoAuthHeader() {
        Event found = buildEvent(10L, 5L, LocalDateTime.now().plusDays(1));
        when(eventRepository.findAll()).thenReturn(List.of(found));
        when(registrationRepository.countByEventId(10L)).thenReturn(1L);
        when(likeRepository.countByEventId(10L)).thenReturn(4L);

        List<Event> response = eventController.getAllEvents(null);

        assertEquals(1, response.size());
        Event body = response.get(0);
        assertSame(found, body);
        assertEquals(1, body.getParticipantsCount());
        assertEquals(4, body.getLikesCount());
        assertEquals(null, body.getIsRegistered());
        assertEquals(null, body.getIsLiked());
    }

    @Test
    void getAllEvents_shouldIgnoreInvalidToken_andReturnEvents() {
        Event found = buildEvent(11L, 7L, LocalDateTime.now().plusDays(1));
        when(eventRepository.findAll()).thenReturn(List.of(found));
        when(jwtUtils.getEmailFromJwtToken("bad-token")).thenThrow(new RuntimeException("bad jwt"));
        when(registrationRepository.countByEventId(11L)).thenReturn(2L);
        when(likeRepository.countByEventId(11L)).thenReturn(3L);

        List<Event> response = eventController.getAllEvents("Bearer bad-token");

        assertEquals(1, response.size());
        assertEquals(2, response.get(0).getParticipantsCount());
        assertEquals(3, response.get(0).getLikesCount());
        assertEquals(null, response.get(0).getIsRegistered());
        assertEquals(null, response.get(0).getIsLiked());
    }

    @Test
    void getAllEvents_shouldFilterCancelledEvents() {
        Event active = buildEvent(20L, 5L, LocalDateTime.now().plusDays(1));
        Event cancelled = buildEvent(21L, 5L, LocalDateTime.now().plusDays(2));
        cancelled.setCancelled(true);

        when(eventRepository.findAll()).thenReturn(List.of(active, cancelled));
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

        when(eventRepository.findAll()).thenReturn(List.of(found));
        when(registrationRepository.countByEventId(22L)).thenReturn(8L);
        when(likeRepository.countByEventId(22L)).thenReturn(13L);
        when(registrationRepository.existsByUserIdAndEventId(5L, 22L)).thenReturn(true);
        when(likeRepository.existsByUserIdAndEventId(5L, 22L)).thenReturn(false);

        List<Event> response = eventController.getAllEvents("Bearer token");

        assertEquals(1, response.size());
        Event body = response.get(0);
        assertEquals(8, body.getParticipantsCount());
        assertEquals(13, body.getLikesCount());
        assertTrue(body.getIsRegistered());
        assertFalse(body.getIsLiked());
    }

    @Test
    void createEvent_shouldReturnSavedEntity() {
        mockCurrentUser(5L);

        EventRequest input = new EventRequest();
        input.setTitle("Input");
        input.setDescription("Description");
        input.setDate(LocalDateTime.now().plusDays(1));
        input.setLocation("Lyon");
        input.setCity("Lyon");
        input.setOnline(false);
        input.setImageUrl("image.png");
        input.setPrice(12.5);
        input.setMaxParticipants(50);
        input.setCategory("Tech");

        Event saved = new Event();
        saved.setId(1L);
        saved.setTitle("Saved");

        when(eventRepository.save(any(Event.class))).thenReturn(saved);

        ResponseEntity<Event> response = eventController.createEvent(input, "Bearer token");

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertSame(saved, response.getBody());
    }

    @Test
    void updateEvent_shouldReturnOk_whenOwnerMatches() {
        mockCurrentUser(5L);
        Event found = buildEvent(12L, 5L, LocalDateTime.now().plusDays(1));
        when(eventRepository.findById(12L)).thenReturn(Optional.of(found));
        when(eventRepository.save(found)).thenReturn(found);

        EventRequest input = new EventRequest();
        input.setTitle("Updated");
        input.setDescription("Desc");
        input.setDate(LocalDateTime.now().plusDays(2));
        input.setLocation("Lyon");
        input.setCity("Lyon");
        input.setOnline(true);
        input.setImageUrl("new.png");
        input.setPrice(20.0);
        input.setMaxParticipants(25);
        input.setCategory("Music");

        ResponseEntity<?> response = eventController.updateEvent(12L, input, "Bearer token");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Event body = assertInstanceOf(Event.class, response.getBody());
        assertEquals("Updated", body.getTitle());
        assertTrue(body.isOnline());
    }

    @Test
    void updateEvent_shouldReturnForbidden_whenOwnerMismatch() {
        mockCurrentUser(5L);
        Event found = buildEvent(12L, 9L, LocalDateTime.now().plusDays(1));
        when(eventRepository.findById(12L)).thenReturn(Optional.of(found));

        ResponseEntity<?> response = eventController.updateEvent(12L, new EventRequest(), "Bearer token");

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    void updateEvent_shouldReturnNotFound_whenEventMissing() {
        mockCurrentUser(5L);
        when(eventRepository.findById(12L)).thenReturn(Optional.empty());

        ResponseEntity<?> response = eventController.updateEvent(12L, new EventRequest(), "Bearer token");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void deleteEvent_shouldReturnNoContent_whenOwnerMatches() {
        mockCurrentUser(5L);
        Event found = buildEvent(13L, 5L, LocalDateTime.now().plusDays(1));
        when(eventRepository.findById(13L)).thenReturn(Optional.of(found));

        ResponseEntity<?> response = eventController.deleteEvent(13L, "Bearer token");

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void deleteEvent_shouldReturnForbidden_whenOwnerMismatch() {
        mockCurrentUser(5L);
        Event found = buildEvent(13L, 99L, LocalDateTime.now().plusDays(1));
        when(eventRepository.findById(13L)).thenReturn(Optional.of(found));

        ResponseEntity<?> response = eventController.deleteEvent(13L, "Bearer token");

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    void deleteEvent_shouldReturnNotFound_whenEventMissing() {
        mockCurrentUser(5L);
        when(eventRepository.findById(13L)).thenReturn(Optional.empty());

        ResponseEntity<?> response = eventController.deleteEvent(13L, "Bearer token");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void register_shouldReturnCreated_whenSuccess() {
        mockCurrentUser(5L);
        Event found = buildEvent(14L, 7L, LocalDateTime.now().plusDays(1));
        when(eventRepository.findById(14L)).thenReturn(Optional.of(found));
        when(registrationRepository.existsByUserIdAndEventId(5L, 14L)).thenReturn(false);
        when(registrationRepository.countByEventId(14L)).thenReturn(1L);

        ResponseEntity<?> response = eventController.register(14L, "Bearer token");

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Map<?, ?> body = assertInstanceOf(Map.class, response.getBody());
        assertEquals(true, body.get("registered"));
        assertEquals(1L, body.get("participantsCount"));
    }

    @Test
    void register_shouldReturnNotFound_whenEventMissing() {
        mockCurrentUser(5L);
        when(eventRepository.findById(14L)).thenReturn(Optional.empty());

        ResponseEntity<?> response = eventController.register(14L, "Bearer token");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void register_shouldReturnBadRequest_whenPastEvent() {
        mockCurrentUser(5L);
        Event past = buildEvent(14L, 7L, LocalDateTime.now().minusDays(1));
        when(eventRepository.findById(14L)).thenReturn(Optional.of(past));

        ResponseEntity<?> response = eventController.register(14L, "Bearer token");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void register_shouldReturnBadRequest_whenAlreadyRegistered() {
        mockCurrentUser(5L);
        Event found = buildEvent(14L, 7L, LocalDateTime.now().plusDays(1));
        when(eventRepository.findById(14L)).thenReturn(Optional.of(found));
        when(registrationRepository.existsByUserIdAndEventId(5L, 14L)).thenReturn(true);

        ResponseEntity<?> response = eventController.register(14L, "Bearer token");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void register_shouldReturnCreated_whenDateIsNull() {
        mockCurrentUser(5L);
        Event found = buildEvent(30L, 7L, null);
        when(eventRepository.findById(30L)).thenReturn(Optional.of(found));
        when(registrationRepository.existsByUserIdAndEventId(5L, 30L)).thenReturn(false);
        when(registrationRepository.countByEventId(30L)).thenReturn(2L);

        ResponseEntity<?> response = eventController.register(30L, "Bearer token");

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Map<?, ?> body = assertInstanceOf(Map.class, response.getBody());
        assertEquals(true, body.get("registered"));
        assertEquals(2L, body.get("participantsCount"));
    }

    @Test
    void getEventById_shouldReturnOkAndEnrichEvent_whenFound() {
        mockCurrentUser(5L);
        Event found = buildEvent(10L, 5L, LocalDateTime.now().plusDays(1));
        when(registrationRepository.countByEventId(10L)).thenReturn(1L);
        when(likeRepository.countByEventId(10L)).thenReturn(4L);
        when(registrationRepository.existsByUserIdAndEventId(5L, 10L)).thenReturn(true);
        when(likeRepository.existsByUserIdAndEventId(5L, 10L)).thenReturn(true);
        when(eventRepository.findById(10L)).thenReturn(Optional.of(found));

        ResponseEntity<?> response = eventController.getEventById(10L, "Bearer token");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Event body = assertInstanceOf(Event.class, response.getBody());
        assertSame(found, body);
        assertEquals(1, body.getParticipantsCount());
        assertEquals(4, body.getLikesCount());
        assertTrue(body.getIsRegistered());
        assertTrue(body.getIsLiked());
    }

    @Test
    void getEventById_shouldReturnNotFound_whenMissing() {
        when(eventRepository.findById(99L)).thenReturn(Optional.empty());

        ResponseEntity<?> response = eventController.getEventById(99L, "Bearer token");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void unregister_shouldReturnOkAndKeepParticipantsCount() {
        mockCurrentUser(5L);
        when(registrationRepository.findByUserIdAndEventId(5L, 5L))
                .thenReturn(Optional.of(new Registration(5L, 5L)));
        when(registrationRepository.countByEventId(5L)).thenReturn(0L);

        ResponseEntity<?> response = eventController.unregister(5L, "Bearer token");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<?, ?> body = assertInstanceOf(Map.class, response.getBody());
        assertFalse((Boolean) body.get("registered"));
        assertEquals(0L, body.get("participantsCount"));
    }

    @Test
    void like_shouldCreateLikeWhenMissing() {
        mockCurrentUser(5L);
        when(likeRepository.existsByUserIdAndEventId(5L, 6L)).thenReturn(false);
        when(likeRepository.countByEventId(6L)).thenReturn(1L);

        ResponseEntity<?> response = eventController.like(6L, "Bearer token");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<?, ?> body = assertInstanceOf(Map.class, response.getBody());
        assertTrue((Boolean) body.get("liked"));
        assertEquals(1L, body.get("likesCount"));
    }

    @Test
    void like_shouldNotCreateDuplicateLike_whenAlreadyExists() {
        mockCurrentUser(5L);
        when(likeRepository.existsByUserIdAndEventId(5L, 6L)).thenReturn(true);
        when(likeRepository.countByEventId(6L)).thenReturn(1L);

        ResponseEntity<?> response = eventController.like(6L, "Bearer token");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(likeRepository, never()).save(any(Like.class));
    }

    @Test
    void unlike_shouldDeleteLikeWhenPresent() {
        mockCurrentUser(5L);
        when(likeRepository.findByUserIdAndEventId(5L, 7L))
                .thenReturn(Optional.of(new Like(5L, 7L)));
        when(likeRepository.countByEventId(7L)).thenReturn(0L);

        ResponseEntity<?> response = eventController.unlike(7L, "Bearer token");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<?, ?> body = assertInstanceOf(Map.class, response.getBody());
        assertFalse((Boolean) body.get("liked"));
        assertEquals(0L, body.get("likesCount"));
    }

    @Test
    void getEventRegistrations_shouldReturnUsers_whenRequesterIsCreator() {
        mockCurrentUser(5L);
        Event event = buildEvent(40L, 5L, LocalDateTime.now().plusDays(1));
        User attendee = new User();
        attendee.setId(12L);
        attendee.setEmail("attendee@example.com");

        when(eventRepository.findById(40L)).thenReturn(Optional.of(event));
        when(registrationRepository.findByEventId(40L)).thenReturn(List.of(new Registration(12L, 40L)));
        when(userRepository.findAllById(List.of(12L))).thenReturn(List.of(attendee));

        ResponseEntity<?> response = eventController.getEventRegistrations(40L, "Bearer token");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<?> body = assertInstanceOf(List.class, response.getBody());
        assertEquals(1, body.size());
        
        // On vérifie que c'est un UserProfileResponse et pas un User
        UserProfileResponse userRes = assertInstanceOf(UserProfileResponse.class, body.get(0));
        assertEquals("attendee@example.com", userRes.getEmail());
    }

    @Test
    void getEventRegistrations_shouldReturnForbidden_whenRequesterIsNotCreator() {
        mockCurrentUser(5L);
        Event event = buildEvent(41L, 9L, LocalDateTime.now().plusDays(1));
        when(eventRepository.findById(41L)).thenReturn(Optional.of(event));

        ResponseEntity<?> response = eventController.getEventRegistrations(41L, "Bearer token");

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        // On vérifie qu'on a bien un message d'erreur JSON
        Map<?, ?> body = assertInstanceOf(Map.class, response.getBody());
        assertEquals("Action interdite : vous n'êtes pas le créateur.", body.get("error"));
    }

    @Test
    void getEventRegistrations_shouldReturnNotFound_whenEventMissing() {
        mockCurrentUser(5L);
        when(eventRepository.findById(42L)).thenReturn(Optional.empty());

        ResponseEntity<?> response = eventController.getEventRegistrations(42L, "Bearer token");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        
        // On vérifie qu'on a bien un message d'erreur JSON au lieu de null
        Map<?, ?> body = assertInstanceOf(Map.class, response.getBody());
        assertEquals("L'évènement est introuvable.", body.get("error"));
    }
}