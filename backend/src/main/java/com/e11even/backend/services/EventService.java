package com.e11even.backend.services;

import com.e11even.backend.models.Event;
import com.e11even.backend.models.Like;
import com.e11even.backend.models.Registration;
import com.e11even.backend.models.User; 
import com.e11even.backend.repositories.EventRepository;
import com.e11even.backend.repositories.LikeRepository;
import com.e11even.backend.repositories.RegistrationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private RegistrationRepository registrationRepository;

    @Autowired
    private LikeRepository likeRepository;

    // --- Helpers de sécurité ---
    
    private boolean isPastEvent(Event event) {
        if (event.getDate() == null) return false;
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime normalizedEventDate = event.getDate().withYear(now.getYear());
        return normalizedEventDate.isBefore(now);
    }

    // --- CRUD Événements ---

    public List<Event> search(String q, String category, String city, Boolean isOnline) {
        return eventRepository.search(q, category, city, isOnline);
    }

    public List<Event> findAll() {
        return eventRepository.findAll();
    }

    public Optional<Event> findById(Long id) {
        return eventRepository.findById(id);
    }

    public Event create(Event event, Long creatorId) {
        event.setCreatorId(creatorId);
        return eventRepository.save(event);
    }

    public Event update(Long id, Event updated, User currentUser) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Évènement introuvable"));
                
        boolean isCreator = event.getCreatorId().equals(currentUser.getId());
        boolean isAdmin = "admin".equalsIgnoreCase(currentUser.getRole());

        if (!isCreator && !isAdmin) {
            throw new RuntimeException("Non autorisé : Seul le créateur ou un Admin peut modifier.");
        }
        
        event.setTitle(updated.getTitle());
        event.setDescription(updated.getDescription());
        event.setDate(updated.getDate());
        event.setLocation(updated.getLocation());
        event.setCity(updated.getCity());
        event.setOnline(updated.isOnline());
        event.setImageUrl(updated.getImageUrl());
        event.setPrice(updated.getPrice());
        event.setMaxParticipants(updated.getMaxParticipants());
        event.setCategory(updated.getCategory());
        return eventRepository.save(event);
    }

    public void delete(Long id, User currentUser) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Évènement introuvable"));
                
        boolean isCreator = event.getCreatorId().equals(currentUser.getId());
        boolean isAdmin = "admin".equalsIgnoreCase(currentUser.getRole());

        if (!isCreator && !isAdmin) {
            throw new RuntimeException("Non autorisé : Seul le créateur ou un Admin peut supprimer.");
        }
        
        eventRepository.delete(event);
    }

    // --- Inscriptions ---

    public Map<String, Object> register(Long eventId, Long userId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Évènement introuvable"));

        if (isPastEvent(event)) {
            throw new RuntimeException("Impossible de s'inscrire à un évènement déjà passé.");
        }

        if (registrationRepository.existsByUserIdAndEventId(userId, eventId)) {
            throw new RuntimeException("Déjà inscrit");
        }

        if (event.getMaxParticipants() != null && event.getMaxParticipants() > 0) {
            long count = registrationRepository.countByEventId(eventId);
            if (count >= event.getMaxParticipants()) {
                throw new RuntimeException("Cet évènement est complet.");
            }
        }

        registrationRepository.save(new Registration(userId, eventId));

        Map<String, Object> result = new HashMap<>();
        result.put("registered", true);
        result.put("participantsCount", registrationRepository.countByEventId(eventId));
        return result;
    }

    public Map<String, Object> unregister(Long eventId, Long userId) {
        Registration reg = registrationRepository.findByUserIdAndEventId(userId, eventId)
                .orElseThrow(() -> new RuntimeException("Inscription introuvable"));
        registrationRepository.delete(reg);

        Map<String, Object> result = new HashMap<>();
        result.put("registered", false);
        result.put("participantsCount", registrationRepository.countByEventId(eventId));
        return result;
    }

    // --- Likes ---

    public Map<String, Object> like(Long eventId, Long userId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Évènement introuvable"));

        if (!likeRepository.existsByUserIdAndEventId(userId, event.getId())) {
            likeRepository.save(new Like(userId, event.getId()));
        }

        Map<String, Object> result = new HashMap<>();
        result.put("liked", true);
        result.put("likesCount", likeRepository.countByEventId(event.getId()));
        return result;
    }

    public Map<String, Object> unlike(Long eventId, Long userId) {
        likeRepository.findByUserIdAndEventId(userId, eventId)
                .ifPresent(likeRepository::delete);

        Map<String, Object> result = new HashMap<>();
        result.put("liked", false);
        result.put("likesCount", likeRepository.countByEventId(eventId));
        return result;
    }
}