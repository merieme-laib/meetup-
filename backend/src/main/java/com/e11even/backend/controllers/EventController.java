package com.e11even.backend.controllers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.e11even.backend.dto.EventRequest;
import com.e11even.backend.models.Event;
import com.e11even.backend.models.Like;
import com.e11even.backend.models.Registration;
import com.e11even.backend.repositories.EventRepository;
import com.e11even.backend.repositories.LikeRepository;
import com.e11even.backend.repositories.RegistrationRepository;
import com.e11even.backend.repositories.UserRepository;
import com.e11even.backend.security.JwtUtils;

import io.swagger.v3.oas.annotations.Parameter;

import com.e11even.backend.models.User;

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

    /**
     * Récupère l'ID de l'utilisateur à partir du Token JWT
     */
    private Long getCurrentUserId(String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String email = jwtUtils.getEmailFromJwtToken(token);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"))
                .getId();
    }

    /**
     * Ajoute les compteurs et les états (like/inscription) à l'objet Event
     */
    private void enrichEvent(Event event, Long userId) {
        event.setParticipantsCount((int) registrationRepository.countByEventId(event.getId()));
        event.setLikesCount((int) likeRepository.countByEventId(event.getId()));
        if (userId != null) {
            event.setIsRegistered(registrationRepository.existsByUserIdAndEventId(userId, event.getId()));
            event.setIsLiked(likeRepository.existsByUserIdAndEventId(userId, event.getId()));
        }
    }

    private boolean isPastEvent(Event event) {
        if (event.getDate() == null) {
            return false;
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime normalizedEventDate = event.getDate().withYear(now.getYear());
        return normalizedEventDate.isBefore(now);
    }

    //  LISTE DES ÉVÈNEMENTS 
    @GetMapping
    public List<Event> getAllEvents(
            @Parameter(hidden = true) @RequestHeader(value = "Authorization", required = false) String authHeader) {
        Long userId = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            try { userId = getCurrentUserId(authHeader); } catch (Exception ignored) {}
        }
        Long finalUserId = userId;
        
        List<Event> events = eventRepository.findAll().stream()
            .filter(event -> !event.isCancelled())
            .collect(java.util.stream.Collectors.toList());
        
        events.forEach(event -> enrichEvent(event, finalUserId));
        return events;
    }

    // DÉTAIL D'UN ÉVÈNEMENT
    @GetMapping("/{id}")
    public ResponseEntity<?> getEventById(
            @PathVariable Long id,
            @Parameter(hidden = true) @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        Long userId = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            try { userId = getCurrentUserId(authHeader); } catch (Exception ignored) {}
        }
        
        Optional<Event> eventOpt = eventRepository.findById(id);
        if (eventOpt.isPresent()) {
            Event event = eventOpt.get();
            enrichEvent(event, userId);
            return ResponseEntity.ok(event);
        } else {
            return ResponseEntity.status(404).body(Map.of("error", "Cet évènement n'existe pas ou a été supprimé"));
        }
    }

    // CRÉATION D'UN ÉVÈNEMENT 
    @PostMapping
    public ResponseEntity<Event> createEvent(
            @RequestBody EventRequest request,
            @Parameter(hidden = true) @RequestHeader("Authorization") String authHeader) {
        
        Long userId = getCurrentUserId(authHeader);
        
        Event event = new Event();
        event.setTitle(request.getTitle());
        event.setDescription(request.getDescription());
        event.setDate(request.getDate());
        event.setLocation(request.getLocation());
        event.setCity(request.getCity());
        event.setOnline(request.isOnline());
        event.setImageUrl(request.getImageUrl());
        event.setPrice(request.getPrice());
        event.setMaxParticipants(request.getMaxParticipants());
        event.setCategory(request.getCategory());
        
        // On force le créateur à être l'utilisateur connecté
        event.setCreatorId(userId);
        
        return ResponseEntity.status(201).body(eventRepository.save(event));
    }

    // MODIFICATION 
    @PutMapping("/{id}")
    public ResponseEntity<?> updateEvent(
            @PathVariable Long id,
            @RequestBody EventRequest request,
            @Parameter(hidden = true) @RequestHeader("Authorization") String authHeader) {
        
        Long currentUserId = getCurrentUserId(authHeader);
        Optional<Event> eventOpt = eventRepository.findById(id);

        if (eventOpt.isPresent()) {
            Event event = eventOpt.get();
            
            // Sécurité : Vérification de propriété 
            if (!event.getCreatorId().equals(currentUserId)) {
                return ResponseEntity.status(403).body(Map.of("error", "Vous n'êtes pas le créateur de cet évènement. Modification refusée."));
            }

            // Mise à jour des champs autorisés
            event.setTitle(request.getTitle());
            event.setDescription(request.getDescription());
            event.setDate(request.getDate());
            event.setLocation(request.getLocation());
            event.setCity(request.getCity());
            event.setOnline(request.isOnline());
            event.setImageUrl(request.getImageUrl());
            event.setPrice(request.getPrice());
            event.setMaxParticipants(request.getMaxParticipants());
            event.setCategory(request.getCategory());
            
            return ResponseEntity.ok(eventRepository.save(event));
        } else {
            return ResponseEntity.status(404).body(Map.of("error", "L'évènement que vous essayez de modifier n'existe pas."));
        }
    }

    // SUPPRESSION 
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEvent(
            @PathVariable Long id,
            @Parameter(hidden = true) @RequestHeader("Authorization") String authHeader) {
        
        Long currentUserId = getCurrentUserId(authHeader);
        Optional<Event> eventOpt = eventRepository.findById(id);

        if (eventOpt.isPresent()) {
            Event event = eventOpt.get();
            
            // Sécurité : Seul le créateur peut supprimer
            if (!event.getCreatorId().equals(currentUserId)) {
                return ResponseEntity.status(403).body(Map.of("error", "Action interdite : vous ne pouvez pas supprimer l'évènement de quelqu'un d'autre."));
            }
            
            eventRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(404).body(Map.of("error", "L'évènement que vous essayez de supprimer est introuvable."));
        }
    }

    // INSCRIPTION
    @PostMapping("/{id}/register")
    public ResponseEntity<?> register(
            @PathVariable Long id,
            @Parameter(hidden = true) @RequestHeader("Authorization") String authHeader) {
        
        Long userId = getCurrentUserId(authHeader);

        Optional<Event> eventOpt = eventRepository.findById(id);
        if (eventOpt.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("error", "Cet évènement n'existe pas."));
        }

        Event event = eventOpt.get();
        if (isPastEvent(event)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Impossible de s'inscrire à un évènement déjà passé."));
        }
        
        if (registrationRepository.existsByUserIdAndEventId(userId, id)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Vous êtes déjà inscrit à cet évènement."));
        }
        
        registrationRepository.save(new Registration(userId, id));
        return ResponseEntity.status(201).body(Map.of(
            "registered", true,
            "participantsCount", registrationRepository.countByEventId(id)
        ));
    }

    // DÉSINCRIPTION
    @DeleteMapping("/{id}/register")
    public ResponseEntity<?> unregister(
            @PathVariable Long id,
            @Parameter(hidden = true) @RequestHeader("Authorization") String authHeader) {
        
        Long userId = getCurrentUserId(authHeader);
        registrationRepository.findByUserIdAndEventId(userId, id)
                .ifPresent(registrationRepository::delete);
                
        return ResponseEntity.ok(Map.of(
            "registered", false,
            "participantsCount", registrationRepository.countByEventId(id)
        ));
    }

    // LIKE
    @PostMapping("/{id}/like")
    public ResponseEntity<?> like(
            @PathVariable Long id,
            @Parameter(hidden = true) @RequestHeader("Authorization") String authHeader) {
        
        Long userId = getCurrentUserId(authHeader);
        if (!likeRepository.existsByUserIdAndEventId(userId, id)) {
            likeRepository.save(new Like(userId, id));
        }
        
        return ResponseEntity.ok(Map.of(
            "liked", true,
            "likesCount", likeRepository.countByEventId(id)
        ));
    }

    //UNLIKE
    @DeleteMapping("/{id}/like")
    public ResponseEntity<?> unlike(
            @PathVariable Long id,
            @Parameter(hidden = true) @RequestHeader("Authorization") String authHeader) {
        
        Long userId = getCurrentUserId(authHeader);
        likeRepository.findByUserIdAndEventId(userId, id)
                .ifPresent(likeRepository::delete);
                
        return ResponseEntity.ok(Map.of(
            "liked", false,
            "likesCount", likeRepository.countByEventId(id)
        ));
    }

    @GetMapping("/{id}/registrations")
    public ResponseEntity<?> getEventRegistrations(
            @PathVariable Long id,
            @Parameter(hidden = true) @RequestHeader("Authorization") String authHeader) {
        Long userId = getCurrentUserId(authHeader);
        
        return eventRepository.findById(id).map(event -> {
            if (!event.getCreatorId().equals(userId)) {
                // On remet un vrai message JSON
                return ResponseEntity.status(403).body(Map.of("error", "Action interdite : vous n'êtes pas le créateur."));
            }
            List<Registration> registrations = registrationRepository.findByEventId(id);
            List<Long> userIds = registrations.stream()
                    .map(Registration::getUserId)
                    .collect(java.util.stream.Collectors.toList());
                    
            List<com.e11even.backend.dto.UserProfileResponse> users = userRepository.findAllById(userIds).stream()
                    .map(com.e11even.backend.dto.UserProfileResponse::new)
                    .collect(java.util.stream.Collectors.toList());
                    
            return ResponseEntity.ok(users);
        }).orElse(ResponseEntity.status(404).body(Map.of("error", "L'évènement est introuvable.")));
    }
}