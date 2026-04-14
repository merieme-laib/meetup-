package com.e11even.backend.controllers;

import com.e11even.backend.models.User;
import com.e11even.backend.security.JwtUtils;
import com.e11even.backend.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map; 

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtUtils jwtUtils; 

    // --- C'EST ICI QUE ÇA A CHANGÉ ---
    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody User user) {
        // 1. Le service inscrit le nouvel utilisateur
        User newUser = authService.register(user);

        // 2. On lui fabrique direct son jeton JWT
        String token = jwtUtils.generateJwtToken(newUser.getEmail());

        // 3. On prépare le même colis que pour le login
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("user", newUser);

        // 4. On envoie
        return response;
    }
    // ---------------------------------

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody User loginRequest) {
        // 1. Le service vérifie l'email et le mot de passe
        User user = authService.login(loginRequest.getEmail(), loginRequest.getPassword());

        // 2. Si c'est bon, on fabrique le jeton JWT avec l'email de l'utilisateur
        String token = jwtUtils.generateJwtToken(user.getEmail());

        // 3. On prépare un colis pour le Frontend contenant le Token ET l'Utilisateur
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("user", user);

        // 4. On envoie 
        return response;
    }
}