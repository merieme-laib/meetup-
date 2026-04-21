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

import com.e11even.backend.dto.EventRequest;
import com.e11even.backend.models.Event;
import com.e11even.backend.models.Like;
import com.e11even.backend.models.Registration;
import com.e11even.backend.repositories.EventRepository;
import com.e11even.backend.repositories.LikeRepository;
import com.e11even.backend.repositories.RegistrationRepository;
import com.e11even.backend.repositories.UserRepository;
import com.e11even.backend.security.JwtUtils;

// 👇 Le fameux import pour nettoyer Swagger 👇
import io.swagger.v3.oas.annotations.Parameter;

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

    private Long getCurrentUserId(String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String email = jwtUtils.getEmailFromJwtToken(token);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"))
                .getId();
    }

    private void enrichEvent(Event event, Long userId) {
        event.setParticipantsCount((int) registrationRepository.countByEventId(event.getId()));
        event.setLikesCount((int) likeRepository.countByEventId(event.getId()));
        if (userId != null) {
            event.setIsRegistered(registrationRepository.existsByUserIdAndEventId(userId, event.getId()));
            event.setIsLiked(likeRepository.existsByUserIdAndEventId(userId, event.getId()));
        }
    }

    // 1. LISTE (Hybride : public ou connecté)
    @GetMapping
    public List<Event> getAllEvents(
            @Parameter(hidden = true) @RequestHeader(value = "Authorization", required = false) String authHeader) {
        List<Event> events = eventRepository.findAll();
        Long userId = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            try { userId = getCurrentUserId(authHeader); } catch (Exception ignored) {}
        }
        Long finalUserId = userId;
        events.forEach(event -> enrichEvent(event, finalUserId));
        return events;
    }

    // 2. DÉTAIL (Hybride)
    @GetMapping("/{id}")
    public ResponseEntity<Event> getEventById(
            @PathVariable Long id,
            @Parameter(hidden = true) @RequestHeader(value = "Authorization", required = false) String authHeader) {
        Long userId = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            try { userId = getCurrentUserId(authHeader); } catch (Exception ignored) {}
        }
        Long finalUserId = userId;
        return eventRepository.findById(id).map(event -> {
            enrichEvent(event, finalUserId);
            return ResponseEntity.ok(event);
        }).orElse(ResponseEntity.notFound().build());
    }

    // 3. CRÉER (Sécurisé avec DTO)
    @PostMapping
    public ResponseEntity<Event> createEvent(
            @RequestBody EventRequest request,
            @Parameter(hidden = true) @RequestHeader("Authorization") String authHeader) {
        
        Long userId = getCurrentUserId(authHeader);
        
        // On transfère les infos du DTO vers un vrai objet Event
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
        
        // Sécurité : on force le créateur à être l'utilisateur connecté
        event.setCreatorId(userId);
        
        return ResponseEntity.status(201).body(eventRepository.save(event));
    }

    // 4. MODIFIER (Sécurisé avec DTO)
    @PutMapping("/{id}")
    public ResponseEntity<Event> updateEvent(
            @PathVariable Long id,
            @RequestBody EventRequest request,
            @Parameter(hidden = true) @RequestHeader("Authorization") String authHeader) {
        
        Long currentUserId = getCurrentUserId(authHeader);

        return eventRepository.findById(id).map(event -> {
            // Sécurité : est-ce que c'est bien SON évènement ?
            if (!event.getCreatorId().equals(currentUserId)) {
                return ResponseEntity.status(403).<Event>build();
            }

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
        }).orElse(ResponseEntity.notFound().build());
    }

    // 5. SUPPRIMER (Sécurisé contre les Hackers)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(
            @PathVariable Long id,
            @Parameter(hidden = true) @RequestHeader("Authorization") String authHeader) {
        
        Long currentUserId = getCurrentUserId(authHeader);

        return eventRepository.findById(id).map(event -> {
            // Sécurité : est-ce que c'est bien SON évènement ?
            if (!event.getCreatorId().equals(currentUserId)) {
                return ResponseEntity.status(403).<Void>build();
            }
            eventRepository.deleteById(id);
            return ResponseEntity.noContent().<Void>build();
        }).orElse(ResponseEntity.notFound().build());
    }

    // 6. S'INSCRIRE
    @PostMapping("/{id}/register")
    public ResponseEntity<?> register(
            @PathVariable Long id,
            @Parameter(hidden = true) @RequestHeader("Authorization") String authHeader) {
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
            @Parameter(hidden = true) @RequestHeader("Authorization") String authHeader) {
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

    // 9. UNLIKER
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
}