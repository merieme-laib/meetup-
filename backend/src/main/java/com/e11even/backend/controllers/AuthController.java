package com.e11even.backend.controllers;

import com.e11even.backend.models.User;
import com.e11even.backend.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public User register(@RequestBody User user) {
        return authService.register(user);
    }

    @PostMapping("/login")
    public User login(@RequestBody User loginRequest) {
        // Ici, tu dois appeler ton service qui vérifie l'email et le password
        return authService.login(loginRequest.getEmail(), loginRequest.getPassword());
    }
}