package com.e11even.backend.controllers;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

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
