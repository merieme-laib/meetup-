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
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
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
}