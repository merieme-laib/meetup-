package com.e11even.backend.controllers;

import com.e11even.backend.models.User;
import com.e11even.backend.security.JwtUtils;
import com.e11even.backend.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        try {
            // 1. Le service inscrit le nouvel utilisateur
            User newUser = authService.register(user);

            // 2. On lui fabrique direct son jeton JWT
            String token = jwtUtils.generateJwtToken(newUser.getEmail());

            // 3. On prépare le colis
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("user", newUser);

            // 4. On envoie avec un beau statut 200 OK
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            // Si l'email existe déjà par exemple
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erreur lors de l'inscription : " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User loginRequest) {
        try {
            // 1. Le service vérifie l'email et le mot de passe
            User user = authService.login(loginRequest.getEmail(), loginRequest.getPassword());

            // Sécurité : si le service ne trouve pas l'utilisateur, on lève l'erreur 401
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Email ou mot de passe incorrect"));
            }

            // 2. Si c'est bon, on fabrique le jeton JWT
            String token = jwtUtils.generateJwtToken(user.getEmail());

            // 3. On prépare un colis pour le Frontend
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("user", user);

            // 4. On envoie le colis avec un statut 200 OK
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            // SI ÇA PLANTE (Mauvais mot de passe ou compte inexistant)
            // On force un beau 401 Unauthorized !
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Email ou mot de passe incorrect"));
        }
    }
}