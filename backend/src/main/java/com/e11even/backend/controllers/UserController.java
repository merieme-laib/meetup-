package com.e11even.backend.controllers;

import java.util.List;
import java.util.Map; 

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler; 
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.e11even.backend.dto.UpdateProfileRequest;
import com.e11even.backend.dto.UserProfileResponse;
import com.e11even.backend.models.Event;
import com.e11even.backend.models.Like;
import com.e11even.backend.models.Registration;
import com.e11even.backend.models.User;
import com.e11even.backend.repositories.EventRepository;
import com.e11even.backend.repositories.LikeRepository;
import com.e11even.backend.repositories.RegistrationRepository;
import com.e11even.backend.repositories.UserRepository;
import com.e11even.backend.security.JwtUtils;
import com.e11even.backend.services.UserService;

import io.swagger.v3.oas.annotations.Parameter;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RegistrationRepository registrationRepository;

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private EventRepository eventRepository;

    private User getCurrentUser(String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String email = jwtUtils.getEmailFromJwtToken(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
        if (user.getIsDeleted()) {
            throw new RuntimeException("Ce compte a été supprimé");
        }
        return user;
    }

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getMe(
            @Parameter(hidden = true) @RequestHeader("Authorization") String authHeader) {
        User user = getCurrentUser(authHeader);
        UserProfileResponse safeProfile = new UserProfileResponse(user);
        return ResponseEntity.ok(safeProfile);
    }

    @PutMapping("/me")
    public ResponseEntity<UserProfileResponse> updateMe(
            @Parameter(hidden = true) @RequestHeader("Authorization") String authHeader,
            @RequestBody UpdateProfileRequest request) {
        User user = getCurrentUser(authHeader);

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setBio(request.getBio());
        user.setAvatarUrl(request.getAvatarUrl());

        User saved = userService.update(user.getId(), user);
        UserProfileResponse safeProfile = new UserProfileResponse(saved);
        return ResponseEntity.ok(safeProfile);
    }

    @GetMapping("/me/registrations")
    public ResponseEntity<List<Event>> getMyRegistrations(
            @Parameter(hidden = true) @RequestHeader("Authorization") String authHeader) {
        User user = getCurrentUser(authHeader);
        List<Registration> registrations = registrationRepository.findByUserId(user.getId());
        List<Long> eventIds = registrations.stream()
                .map(Registration::getEventId)
                .collect(java.util.stream.Collectors.toList());
        List<Event> events = eventRepository.findAllById(eventIds);
        return ResponseEntity.ok(events);
    }

    @GetMapping("/me/likes")
    public ResponseEntity<List<Event>> getMyLikes(
            @Parameter(hidden = true) @RequestHeader("Authorization") String authHeader) {
        User user = getCurrentUser(authHeader);
        List<Like> likes = likeRepository.findByUserId(user.getId());
        List<Long> eventIds = likes.stream()
                .map(Like::getEventId)
                .collect(java.util.stream.Collectors.toList());
        List<Event> events = eventRepository.findAllById(eventIds);
        return ResponseEntity.ok(events);
    }

    // PUT /api/users/me/email
    @PutMapping("/me/email")
    public ResponseEntity<?> updateEmail(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody java.util.Map<String, String> body) {
        try {
            User user = getCurrentUser(authHeader);
            User saved = userService.updateEmail(user.getId(), body.get("newEmail"), body.get("password"));
            String newToken = jwtUtils.generateJwtToken(saved.getEmail());
            return ResponseEntity.ok(java.util.Map.of(
                "user", saved,
                "token", newToken
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", e.getMessage()));
        }
    }

    // PUT /api/users/me/password
    @PutMapping("/me/password")
    public ResponseEntity<?> updatePassword(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody java.util.Map<String, String> body) {
        try {
            User user = getCurrentUser(authHeader);
            userService.updatePassword(user.getId(), body.get("currentPassword"), body.get("newPassword"));
            return ResponseEntity.ok(java.util.Map.of("message", "Mot de passe modifié avec succès"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", e.getMessage()));
        }
    }


    @DeleteMapping("/me")
    public ResponseEntity<?> deleteAccount(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody java.util.Map<String, String> body) {
        try {
            User user = getCurrentUser(authHeader);
            
            // Vérifier le mot de passe
            String password = body.get("password");
            if (password == null || !user.getPassword().equals(password)) {
                return ResponseEntity.badRequest().body(java.util.Map.of("error", "Mot de passe incorrect"));
            }
            
            // Annuler tous les événements créés par l'utilisateur
            List<Event> myEvents = eventRepository.findByCreatorId(user.getId());
            myEvents.forEach(event -> {
                event.setCancelled(true);
                eventRepository.save(event);
            });
            
            // Marquer le compte comme supprimé
            user.setDeleted(true);
            userRepository.save(user);
            
            return ResponseEntity.ok(java.util.Map.of("message", "Compte supprimé avec succès"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", e.getMessage()));
        }
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleRuntimeException(RuntimeException ex) {
        return ResponseEntity.status(404).body(Map.of("error", ex.getMessage()));
    }
}
