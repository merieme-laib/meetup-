package com.e11even.backend.controllers;

import com.e11even.backend.dto.EventRequest;
import com.e11even.backend.models.Event;
import com.e11even.backend.models.User;
import com.e11even.backend.repositories.EventRepository;
import com.e11even.backend.repositories.LikeRepository;
import com.e11even.backend.repositories.RegistrationRepository;
import com.e11even.backend.repositories.UserRepository;
import com.e11even.backend.security.JwtUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
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

    @Test
    void getAllEvents_shouldReturnRepositoryResult() {
        Event event = new Event();
        event.setTitle("Meetup");
        event.setId(1L);
        when(eventRepository.findAll()).thenReturn(List.of(event));
        when(registrationRepository.countByEventId(1L)).thenReturn(0L);
        when(likeRepository.countByEventId(1L)).thenReturn(0L);

        List<Event> result = eventController.getAllEvents(null);

        assertEquals(1, result.size());
        assertEquals("Meetup", result.getFirst().getTitle());
    }

    @Test
    void createEvent_shouldReturnSavedEntity() {
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

        User user = new User();
        user.setId(5L);

        Event saved = new Event();
        saved.setId(1L);
        saved.setTitle("Saved");

        when(jwtUtils.getEmailFromJwtToken("token")).thenReturn("john@example.com");
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));
        when(eventRepository.save(any(Event.class))).thenReturn(saved);

        ResponseEntity<Event> response = eventController.createEvent(input, "Bearer token");

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertSame(saved, response.getBody());
    }

    @Test
    void getEventById_shouldReturnOk_whenFound() {
        Event found = new Event();
        found.setId(10L);
        when(eventRepository.findById(10L)).thenReturn(Optional.of(found));
        when(registrationRepository.countByEventId(10L)).thenReturn(0L);
        when(likeRepository.countByEventId(10L)).thenReturn(0L);

        ResponseEntity<?> response = eventController.getEventById(10L, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(found, response.getBody());
    }

    @Test
    void getEventById_shouldReturnNotFound_whenMissing() {
        when(eventRepository.findById(99L)).thenReturn(Optional.empty());

        ResponseEntity<?> response = eventController.getEventById(99L, null);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}