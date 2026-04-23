package com.e11even.backend.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
    }

    // GET /api/users/me
    @GetMapping("/me")
    public ResponseEntity<User> getMe(@RequestHeader("Authorization") String authHeader) {
        User user = getCurrentUser(authHeader);
        return ResponseEntity.ok(user);
    }

    // PUT /api/users/me
    @PutMapping("/me")
    public ResponseEntity<User> updateMe(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody User updated
    ) {
        User user = getCurrentUser(authHeader);
        User saved = userService.update(user.getId(), updated);
        return ResponseEntity.ok(saved);
    }


    // GET /api/users/me/registrations
    @GetMapping("/me/registrations")
    public ResponseEntity<?> getMyRegistrations(@RequestHeader("Authorization") String authHeader) {
        User user = getCurrentUser(authHeader);
        List<Registration> registrations = registrationRepository.findByUserId(user.getId());
        List<Long> eventIds = registrations.stream()
                .map(Registration::getEventId)
                .collect(java.util.stream.Collectors.toList());
        List<Event> events = eventRepository.findAllById(eventIds);
        return ResponseEntity.ok(events);
    }

    // GET /api/users/me/likes
    @GetMapping("/me/likes")
    public ResponseEntity<?> getMyLikes(@RequestHeader("Authorization") String authHeader) {
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

}
