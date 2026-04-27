package com.e11even.backend.controllers;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.e11even.backend.dto.EventRequest;
import com.e11even.backend.models.Event;
import com.e11even.backend.models.Registration;
import com.e11even.backend.models.User;
import com.e11even.backend.repositories.EventRepository;
import com.e11even.backend.repositories.LikeRepository;
import com.e11even.backend.repositories.RegistrationRepository;
import com.e11even.backend.repositories.UserRepository;
import com.e11even.backend.security.JwtUtils;
import com.e11even.backend.services.EventService;

import io.swagger.v3.oas.annotations.Parameter;

@RestController
@RequestMapping("/api/events")
public class EventController {

    @Autowired
    private EventService eventService;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserRepository userRepository;

    // Repositories conservés uniquement pour la méthode enrichEvent et l'affichage des inscrits
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private RegistrationRepository registrationRepository;
    @Autowired
    private LikeRepository likeRepository;

    private Long getCurrentUserId(String authHeader) {
        return getCurrentUserObject(authHeader).getId();
    }

    private User getCurrentUserObject(String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String email = jwtUtils.getEmailFromJwtToken(token);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
    }

    private void enrichEvent(Event event, Long userId) {
        event.setParticipantsCount((int) registrationRepository.countByEventId(event.getId()));
        event.setLikesCount((int) likeRepository.countByEventId(event.getId()));
        if (userId != null) {
            event.setIsRegistered(registrationRepository.existsByUserIdAndEventId(userId, event.getId()));
            event.setIsLiked(likeRepository.existsByUserIdAndEventId(userId, event.getId()));
        }
    }

    // LISTE DES ÉVÈNEMENTS 
    @GetMapping
    public List<Event> getAllEvents(
            @Parameter(hidden = true) @RequestHeader(value = "Authorization", required = false) String authHeader) {
        Long userId = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            try { userId = getCurrentUserId(authHeader); } catch (Exception ignored) {}
        }
        Long finalUserId = userId;
        
        List<Event> events = eventService.findAll().stream()
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
        
        Optional<Event> eventOpt = eventService.findById(id);
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
        
        Event createdEvent = eventService.create(event, userId);
        return ResponseEntity.status(201).body(createdEvent);
    }

    // MODIFICATION 
    @PutMapping("/{id}")
    public ResponseEntity<?> updateEvent(
            @PathVariable Long id,
            @RequestBody EventRequest request,
            @Parameter(hidden = true) @RequestHeader("Authorization") String authHeader) {
        
        try {
            User currentUser = getCurrentUserObject(authHeader);
            
            Event updatedData = new Event();
            updatedData.setTitle(request.getTitle());
            updatedData.setDescription(request.getDescription());
            updatedData.setDate(request.getDate());
            updatedData.setLocation(request.getLocation());
            updatedData.setCity(request.getCity());
            updatedData.setOnline(request.isOnline());
            updatedData.setImageUrl(request.getImageUrl());
            updatedData.setPrice(request.getPrice());
            updatedData.setMaxParticipants(request.getMaxParticipants());
            updatedData.setCategory(request.getCategory());

            Event savedEvent = eventService.update(id, updatedData, currentUser);
            return ResponseEntity.ok(savedEvent);
            
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Non autorisé")) return ResponseEntity.status(403).body(Map.of("error", e.getMessage()));
            if (e.getMessage().contains("introuvable")) return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // SUPPRESSION 
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEvent(
            @PathVariable Long id,
            @Parameter(hidden = true) @RequestHeader("Authorization") String authHeader) {
        
        try {
            User currentUser = getCurrentUserObject(authHeader);
            eventService.delete(id, currentUser);
            return ResponseEntity.noContent().build();
            
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Non autorisé")) return ResponseEntity.status(403).body(Map.of("error", e.getMessage()));
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        }
    }

    // INSCRIPTION
    @PostMapping("/{id}/register")
    public ResponseEntity<?> register(
            @PathVariable Long id,
            @Parameter(hidden = true) @RequestHeader("Authorization") String authHeader) {
        
        try {
            Long userId = getCurrentUserId(authHeader);
            Map<String, Object> result = eventService.register(id, userId);
            return ResponseEntity.status(201).body(result);
            
        } catch (RuntimeException e) {
            if (e.getMessage().contains("introuvable")) return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // DÉSINCRIPTION
    @DeleteMapping("/{id}/register")
    public ResponseEntity<?> unregister(
            @PathVariable Long id,
            @Parameter(hidden = true) @RequestHeader("Authorization") String authHeader) {
        
        try {
            Long userId = getCurrentUserId(authHeader);
            Map<String, Object> result = eventService.unregister(id, userId);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // LIKE
    @PostMapping("/{id}/like")
    public ResponseEntity<?> like(
            @PathVariable Long id,
            @Parameter(hidden = true) @RequestHeader("Authorization") String authHeader) {
        
        try {
            Long userId = getCurrentUserId(authHeader);
            Map<String, Object> result = eventService.like(id, userId);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // UNLIKE
    @DeleteMapping("/{id}/like")
    public ResponseEntity<?> unlike(
            @PathVariable Long id,
            @Parameter(hidden = true) @RequestHeader("Authorization") String authHeader) {
        
        try {
            Long userId = getCurrentUserId(authHeader);
            Map<String, Object> result = eventService.unlike(id, userId);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{id}/registrations")
    public ResponseEntity<?> getEventRegistrations(
            @PathVariable Long id,
            @Parameter(hidden = true) @RequestHeader("Authorization") String authHeader) {
        
        User currentUser = getCurrentUserObject(authHeader);
        
        return eventService.findById(id).map(event -> {
            boolean isCreator = event.getCreatorId().equals(currentUser.getId());
            boolean isAdmin = "admin".equalsIgnoreCase(currentUser.getRole());

            if (!isCreator && !isAdmin) {
                return ResponseEntity.status(403).body(Map.of("error", "Action interdite : vous n'êtes pas autorisé à voir cette liste."));
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