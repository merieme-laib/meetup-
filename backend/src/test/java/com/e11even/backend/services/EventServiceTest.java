package com.e11even.backend.services;

import com.e11even.backend.models.Event;
import com.e11even.backend.models.Like;
import com.e11even.backend.models.Registration;
import com.e11even.backend.repositories.EventRepository;
import com.e11even.backend.repositories.LikeRepository;
import com.e11even.backend.repositories.RegistrationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private RegistrationRepository registrationRepository;

    @Mock
    private LikeRepository likeRepository;

    @InjectMocks
    private EventService eventService;

    private Event event(long id, long creatorId, Integer maxParticipants) {
        Event event = new Event();
        event.setId(id);
        event.setCreatorId(creatorId);
        event.setTitle("Title");
        event.setDescription("Desc");
        event.setDate(LocalDateTime.now().plusDays(2));
        event.setLocation("Campus");
        event.setCity("Lyon");
        event.setOnline(false);
        event.setImageUrl("http://example.com/img.png");
        event.setPrice(10.0);
        event.setMaxParticipants(maxParticipants);
        event.setCategory("Tech");
        return event;
    }

    @Test
    void search_shouldDelegateToRepository() {
        Event e = event(1L, 1L, 10);
        when(eventRepository.search("q", "Tech", "Lyon", false)).thenReturn(List.of(e));

        List<Event> result = eventService.search("q", "Tech", "Lyon", false);

        assertEquals(1, result.size());
        assertSame(e, result.getFirst());
    }

    @Test
    void create_shouldSetCreatorAndSave() {
        Event e = event(1L, 0L, 10);
        when(eventRepository.save(any(Event.class))).thenReturn(e);

        eventService.create(e, 7L);

        ArgumentCaptor<Event> captor = ArgumentCaptor.forClass(Event.class);
        verify(eventRepository).save(captor.capture());
        assertEquals(7L, captor.getValue().getCreatorId());
    }

    @Test
    void update_shouldSave_whenOwnerMatches() {
        Event existing = event(3L, 9L, 10);
        Event updated = event(3L, 9L, 50);
        updated.setTitle("Updated");
        when(eventRepository.findById(3L)).thenReturn(Optional.of(existing));
        when(eventRepository.save(existing)).thenReturn(existing);

        Event result = eventService.update(3L, updated, 9L);

        assertSame(existing, result);
        assertEquals("Updated", existing.getTitle());
        assertEquals(50, existing.getMaxParticipants());
    }

    @Test
    void update_shouldThrow_whenEventMissing() {
        when(eventRepository.findById(3L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> eventService.update(3L, event(3L, 1L, 1), 1L));

        assertEquals("Évènement introuvable", ex.getMessage());
    }

    @Test
    void delete_shouldThrow_whenNotOwner() {
        when(eventRepository.findById(3L)).thenReturn(Optional.of(event(3L, 2L, 10)));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> eventService.delete(3L, 9L));

        assertEquals("Non autorisé", ex.getMessage());
    }

    @Test
    void register_shouldReturnRegisteredAndCount() {
        when(eventRepository.findById(4L)).thenReturn(Optional.of(event(4L, 1L, 10)));
        when(registrationRepository.existsByUserIdAndEventId(5L, 4L)).thenReturn(false);
        when(registrationRepository.countByEventId(4L)).thenReturn(1L);

        Map<String, Object> result = eventService.register(4L, 5L);

        assertTrue((Boolean) result.get("registered"));
        assertEquals(1L, result.get("participantsCount"));
    }

    @Test
    void register_shouldThrow_whenAlreadyRegistered() {
        when(eventRepository.findById(4L)).thenReturn(Optional.of(event(4L, 1L, 10)));
        when(registrationRepository.existsByUserIdAndEventId(5L, 4L)).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> eventService.register(4L, 5L));

        assertEquals("Déjà inscrit", ex.getMessage());
    }

    @Test
    void register_shouldThrow_whenNoSeatsLeft() {
        when(eventRepository.findById(4L)).thenReturn(Optional.of(event(4L, 1L, 1)));
        when(registrationRepository.existsByUserIdAndEventId(5L, 4L)).thenReturn(false);
        when(registrationRepository.countByEventId(4L)).thenReturn(1L);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> eventService.register(4L, 5L));

        assertEquals("Plus de places disponibles", ex.getMessage());
    }

    @Test
    void unregister_shouldReturnRegisteredFalse() {
        when(registrationRepository.findByUserIdAndEventId(5L, 4L)).thenReturn(Optional.of(new Registration(5L, 4L)));
        when(registrationRepository.countByEventId(4L)).thenReturn(0L);

        Map<String, Object> result = eventService.unregister(4L, 5L);

        assertFalse((Boolean) result.get("registered"));
        assertEquals(0L, result.get("participantsCount"));
    }

    @Test
    void like_shouldCreateLikeAndReturnCount() {
        when(eventRepository.findById(4L)).thenReturn(Optional.of(event(4L, 1L, 10)));
        when(likeRepository.existsByUserIdAndEventId(5L, 4L)).thenReturn(false);
        when(likeRepository.countByEventId(4L)).thenReturn(2L);

        Map<String, Object> result = eventService.like(4L, 5L);

        assertTrue((Boolean) result.get("liked"));
        assertEquals(2L, result.get("likesCount"));
        verify(likeRepository).save(any(Like.class));
    }

    @Test
    void unlike_shouldReturnLikedFalse() {
        when(likeRepository.findByUserIdAndEventId(5L, 4L)).thenReturn(Optional.of(new Like(5L, 4L)));
        when(likeRepository.countByEventId(4L)).thenReturn(0L);

        Map<String, Object> result = eventService.unlike(4L, 5L);

        assertFalse((Boolean) result.get("liked"));
        assertEquals(0L, result.get("likesCount"));
    }
}
