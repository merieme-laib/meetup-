package com.e11even.backend.controllers;

import com.e11even.backend.models.Event;
import com.e11even.backend.repositories.EventRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventControllerTest {

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private EventController eventController;

    @Test
    void getAllEvents_shouldReturnRepositoryResult() {
        Event event = new Event();
        event.setTitle("Meetup");
        when(eventRepository.findAll()).thenReturn(List.of(event));

        List<Event> result = eventController.getAllEvents();

        assertEquals(1, result.size());
        assertEquals("Meetup", result.getFirst().getTitle());
    }

    @Test
    void createEvent_shouldReturnSavedEntity() {
        Event input = new Event();
        input.setTitle("Input");

        Event saved = new Event();
        saved.setId(1L);
        saved.setTitle("Saved");

        when(eventRepository.save(input)).thenReturn(saved);

        ResponseEntity<Event> response = eventController.createEvent(input);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(saved, response.getBody());
    }

    @Test
    void getEventById_shouldReturnOk_whenFound() {
        Event found = new Event();
        found.setId(10L);
        when(eventRepository.findById(10L)).thenReturn(Optional.of(found));

        ResponseEntity<Event> response = eventController.getEventById(10L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(found, response.getBody());
    }

    @Test
    void getEventById_shouldReturnNotFound_whenMissing() {
        when(eventRepository.findById(99L)).thenReturn(Optional.empty());

        ResponseEntity<Event> response = eventController.getEventById(99L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}