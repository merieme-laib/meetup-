package com.e11even.backend.controllers;

import com.e11even.backend.dto.EventRequest;
import com.e11even.backend.models.Event;
import com.e11even.backend.models.Like;
import com.e11even.backend.models.Registration;
import com.e11even.backend.models.User;
import com.e11even.backend.repositories.EventRepository;
import com.e11even.backend.repositories.LikeRepository;
import com.e11even.backend.repositories.RegistrationRepository;
import com.e11even.backend.repositories.UserRepository;
import com.e11even.backend.security.JwtUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
        event.setDescription("Description");
        event.setDate(date);
        event.setLocation("Campus");
        event.setCity("Lyon");
        event.setImageUrl("http://example.com/image.png");
        event.setPrice(10.0);
        event.setMaxParticipants(100);
        event.setCategory("Tech");
        event.setOnline(false);
        return event;
    }

    private EventRequest buildRequest() {
        EventRequest input = new EventRequest();
        input.setTitle("Input");
        input.setDescription("Desc");
        input.setDate(LocalDateTime.now().plusDays(1));
        input.setLocation("Campus");
        input.setCity("Lyon");
        input.setImageUrl("http://example.com/image.png");
        input.setPrice(0.0);
        input.setMaxParticipants(100);
        input.setCategory("Tech");
        input.setOnline(false);
        return input;
    }

    @Test
    void getAllEvents_shouldEnrichEventsAndReturnList() {
        mockCurrentUser(5L);
        Event event = buildEvent(1L, 5L, LocalDateTime.now().plusDays(1));
        when(eventRepository.findAll()).thenReturn(List.of(event));
        when(registrationRepository.countByEventId(1L)).thenReturn(2L);
        when(likeRepository.countByEventId(1L)).thenReturn(3L);
        when(registrationRepository.existsByUserIdAndEventId(5L, 1L)).thenReturn(true);
        when(likeRepository.existsByUserIdAndEventId(5L, 1L)).thenReturn(false);

        List<Event> result = eventController.getAllEvents("Bearer token");

        assertEquals(1, result.size());
        assertEquals(2, result.getFirst().getParticipantsCount());
        assertEquals(3, result.getFirst().getLikesCount());
        assertTrue(result.getFirst().getIsRegistered());
        assertFalse(result.getFirst().getIsLiked());
    }

    @Test
    void getAllEvents_shouldIgnoreInvalidAuthHeader() {
        Event event = buildEvent(1L, 5L, LocalDateTime.now().plusDays(1));
        when(jwtUtils.getEmailFromJwtToken("bad-token")).thenThrow(new RuntimeException("bad token"));
        when(eventRepository.findAll()).thenReturn(List.of(event));
        when(registrationRepository.countByEventId(1L)).thenReturn(2L);
        when(likeRepository.countByEventId(1L)).thenReturn(3L);

        List<Event> result = eventController.getAllEvents("Bearer bad-token");

        assertEquals(1, result.size());
        assertEquals(2, result.getFirst().getParticipantsCount());
        assertEquals(3, result.getFirst().getLikesCount());
    }

    @Test
    void getEventById_shouldReturnOkAndEnrichEvent_whenFound() {
        mockCurrentUser(5L);
        Event found = buildEvent(10L, 5L, LocalDateTime.now().plusDays(1));
        when(eventRepository.findById(10L)).thenReturn(Optional.of(found));
        when(registrationRepository.countByEventId(10L)).thenReturn(1L);
        when(likeRepository.countByEventId(10L)).thenReturn(4L);
        when(registrationRepository.existsByUserIdAndEventId(5L, 10L)).thenReturn(true);
        when(likeRepository.existsByUserIdAndEventId(5L, 10L)).thenReturn(true);

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

        ResponseEntity<?> response = eventController.getEventById(99L, null);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        Map<?, ?> body = assertInstanceOf(Map.class, response.getBody());
        assertEquals("Cet évènement n'existe pas ou a été supprimé", body.get("error"));
    }

    @Test
    void getEventById_shouldIgnoreInvalidAuthHeader() {
        Event found = buildEvent(10L, 5L, LocalDateTime.now().plusDays(1));
        when(jwtUtils.getEmailFromJwtToken("bad-token")).thenThrow(new RuntimeException("bad token"));
        when(eventRepository.findById(10L)).thenReturn(Optional.of(found));
        when(registrationRepository.countByEventId(10L)).thenReturn(1L);
        when(likeRepository.countByEventId(10L)).thenReturn(4L);

        ResponseEntity<?> response = eventController.getEventById(10L, "Bearer bad-token");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Event body = assertInstanceOf(Event.class, response.getBody());
        assertEquals(1, body.getParticipantsCount());
        assertEquals(4, body.getLikesCount());
        assertEquals(null, body.getIsRegistered());
        assertEquals(null, body.getIsLiked());
    }

    @Test
    void createEvent_shouldPersistCreatorAndReturnCreated() {
        User user = mockCurrentUser(5L);
        EventRequest input = buildRequest();
        Event saved = buildEvent(1L, 5L, input.getDate());
        when(eventRepository.save(any(Event.class))).thenReturn(saved);

        ResponseEntity<Event> response = eventController.createEvent(input, "Bearer token");

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertSame(saved, response.getBody());
        ArgumentCaptor<Event> captor = ArgumentCaptor.forClass(Event.class);
        verify(eventRepository).save(captor.capture());
        assertEquals(user.getId(), captor.getValue().getCreatorId());
    }

    @Test
    void updateEvent_shouldReturnOkWhenOwnerMatches() {
        mockCurrentUser(5L);
        Event existing = buildEvent(2L, 5L, LocalDateTime.now().plusDays(1));
        EventRequest input = buildRequest();
        input.setTitle("Updated");
        when(eventRepository.findById(2L)).thenReturn(Optional.of(existing));
        when(eventRepository.save(existing)).thenReturn(existing);

        ResponseEntity<?> response = eventController.updateEvent(2L, input, "Bearer token");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Updated", existing.getTitle());
    }

    @Test
    void updateEvent_shouldReturnForbiddenWhenNotOwner() {
        mockCurrentUser(5L);
        Event existing = buildEvent(2L, 99L, LocalDateTime.now().plusDays(1));
        when(eventRepository.findById(2L)).thenReturn(Optional.of(existing));

        ResponseEntity<?> response = eventController.updateEvent(2L, buildRequest(), "Bearer token");

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    void updateEvent_shouldReturnNotFoundWhenMissing() {
        mockCurrentUser(5L);
        when(eventRepository.findById(2L)).thenReturn(Optional.empty());

        ResponseEntity<?> response = eventController.updateEvent(2L, buildRequest(), "Bearer token");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void deleteEvent_shouldReturnNoContentWhenOwnerMatches() {
        mockCurrentUser(5L);
        Event existing = buildEvent(3L, 5L, LocalDateTime.now().plusDays(1));
        when(eventRepository.findById(3L)).thenReturn(Optional.of(existing));

        ResponseEntity<?> response = eventController.deleteEvent(3L, "Bearer token");

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void deleteEvent_shouldReturnForbiddenWhenNotOwner() {
        mockCurrentUser(5L);
        Event existing = buildEvent(3L, 9L, LocalDateTime.now().plusDays(1));
        when(eventRepository.findById(3L)).thenReturn(Optional.of(existing));

        ResponseEntity<?> response = eventController.deleteEvent(3L, "Bearer token");

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    void deleteEvent_shouldReturnNotFoundWhenMissing() {
        mockCurrentUser(5L);
        when(eventRepository.findById(3L)).thenReturn(Optional.empty());

        ResponseEntity<?> response = eventController.deleteEvent(3L, "Bearer token");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void register_shouldCreateRegistration() {
        mockCurrentUser(5L);
        Event existing = buildEvent(4L, 5L, LocalDateTime.now().plusDays(1));
        when(eventRepository.findById(4L)).thenReturn(Optional.of(existing));
        when(registrationRepository.existsByUserIdAndEventId(5L, 4L)).thenReturn(false);
        when(registrationRepository.countByEventId(4L)).thenReturn(1L);

        ResponseEntity<?> response = eventController.register(4L, "Bearer token");

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Map<?, ?> body = assertInstanceOf(Map.class, response.getBody());
        assertEquals(true, body.get("registered"));
        assertEquals(1L, body.get("participantsCount"));
    }

    @Test
    void register_shouldReturnBadRequestWhenPastEvent() {
        mockCurrentUser(5L);
        Event existing = buildEvent(4L, 5L, LocalDateTime.now().minusDays(1));
        when(eventRepository.findById(4L)).thenReturn(Optional.of(existing));

        ResponseEntity<?> response = eventController.register(4L, "Bearer token");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void register_shouldReturnBadRequestWhenAlreadyRegistered() {
        mockCurrentUser(5L);
        Event existing = buildEvent(4L, 5L, LocalDateTime.now().plusDays(1));
        when(eventRepository.findById(4L)).thenReturn(Optional.of(existing));
        when(registrationRepository.existsByUserIdAndEventId(5L, 4L)).thenReturn(true);

        ResponseEntity<?> response = eventController.register(4L, "Bearer token");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void register_shouldReturnNotFoundWhenMissing() {
        mockCurrentUser(5L);
        when(eventRepository.findById(4L)).thenReturn(Optional.empty());

        ResponseEntity<?> response = eventController.register(4L, "Bearer token");

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
        assertEquals(false, body.get("registered"));
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
        assertEquals(true, body.get("liked"));
        assertEquals(1L, body.get("likesCount"));
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
        assertEquals(false, body.get("liked"));
        assertEquals(0L, body.get("likesCount"));
    }
}