package com.e11even.backend.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.e11even.backend.models.Event;
import com.e11even.backend.models.Like;
import com.e11even.backend.models.Registration;
import com.e11even.backend.repositories.EventRepository;
import com.e11even.backend.repositories.LikeRepository;
import com.e11even.backend.repositories.RegistrationRepository;
import com.e11even.backend.repositories.UserRepository;
import com.e11even.backend.security.JwtUtils;

@RestController
@RequestMapping("/api/events")
public class EventController {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private RegistrationRepository registrationRepository;

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserRepository userRepository;

    // Récupère l'ID de l'utilisateur connecté depuis le token JWT
    private Long getCurrentUserId(String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String email = jwtUtils.getEmailFromJwtToken(token);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"))
                .getId();
    }

    // 1. LISTE
    @GetMapping
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    // 2. DÉTAIL
    @GetMapping("/{id}")
    public ResponseEntity<Event> getEventById(@PathVariable Long id) {
        return eventRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 3. CRÉER
    @PostMapping
    public ResponseEntity<Event> createEvent(
            @RequestBody Event event,
            @RequestHeader("Authorization") String authHeader) {
        Long userId = getCurrentUserId(authHeader);
        event.setCreatorId(userId);
        return ResponseEntity.status(201).body(eventRepository.save(event));
    }

    // 4. MODIFIER
    @PutMapping("/{id}")
    public ResponseEntity<Event> updateEvent(
            @PathVariable Long id,
            @RequestBody Event updatedEvent,
            @RequestHeader("Authorization") String authHeader) {
        return eventRepository.findById(id).map(event -> {
            event.setTitle(updatedEvent.getTitle());
            event.setDescription(updatedEvent.getDescription());
            event.setDate(updatedEvent.getDate());
            event.setLocation(updatedEvent.getLocation());
            event.setCity(updatedEvent.getCity());
            event.setOnline(updatedEvent.isOnline());
            event.setImageUrl(updatedEvent.getImageUrl());
            event.setPrice(updatedEvent.getPrice());
            event.setMaxParticipants(updatedEvent.getMaxParticipants());
            event.setCategory(updatedEvent.getCategory());
            return ResponseEntity.ok(eventRepository.save(event));
        }).orElse(ResponseEntity.notFound().build());
    }

    // 5. SUPPRIMER
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        if (!eventRepository.existsById(id)) return ResponseEntity.notFound().build();
        eventRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // 6. S'INSCRIRE
    @PostMapping("/{id}/register")
    public ResponseEntity<?> register(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {
        Long userId = getCurrentUserId(authHeader);
        if (!eventRepository.existsById(id)) return ResponseEntity.notFound().build();
        if (registrationRepository.existsByUserIdAndEventId(userId, id)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Déjà inscrit"));
        }
        registrationRepository.save(new Registration(userId, id));
        return ResponseEntity.status(201).body(Map.of(
            "registered", true,
            "participantsCount", registrationRepository.countByEventId(id)
        ));
    }

    // 7. SE DÉSINSCRIRE
    @DeleteMapping("/{id}/register")
    public ResponseEntity<?> unregister(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {
        Long userId = getCurrentUserId(authHeader);
        registrationRepository.findByUserIdAndEventId(userId, id)
                .ifPresent(registrationRepository::delete);
        return ResponseEntity.ok(Map.of(
            "registered", false,
            "participantsCount", registrationRepository.countByEventId(id)
        ));
    }

    // 8. LIKER
    @PostMapping("/{id}/like")
    public ResponseEntity<?> like(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {
        Long userId = getCurrentUserId(authHeader);
        if (!likeRepository.existsByUserIdAndEventId(userId, id)) {
            likeRepository.save(new Like(userId, id));
        }
        return ResponseEntity.ok(Map.of(
            "liked", true,
            "likesCount", likeRepository.countByEventId(id)
        ));
    }

    // 9. UNLIKER
    @DeleteMapping("/{id}/like")
    public ResponseEntity<?> unlike(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {
        Long userId = getCurrentUserId(authHeader);
        likeRepository.findByUserIdAndEventId(userId, id)
                .ifPresent(likeRepository::delete);
        return ResponseEntity.ok(Map.of(
            "liked", false,
            "likesCount", likeRepository.countByEventId(id)
        ));
    }
}
