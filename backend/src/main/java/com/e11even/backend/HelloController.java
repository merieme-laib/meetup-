package com.e11even.backend;

import com.e11even.backend.models.User;
import com.e11even.backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class HelloController {

    @Autowired
    private UserRepository userRepository; // On "injecte" le robot de sauvegarde

    @GetMapping("/")
    public String sayHello() {
        String testEmail = "oxcar@example.com";

        // On vérifie si cet email existe déjà en base pour éviter le crash
        // (Ici on fait une recherche simple, on verra plus tard comment faire mieux)
        List<User> allUsers = userRepository.findAll();
        boolean alreadyExists = allUsers.stream().anyMatch(u -> u.getEmail().equals(testEmail));

        if (!alreadyExists) {
            User newUser = new User();
            newUser.setName("Oxcar");
            newUser.setEmail(testEmail);
            newUser.setPassword("password123");
            userRepository.save(newUser);
        }

        return "<h1>Backend Connecté ! 🚀</h1>" +
                "<p>Utilisateurs en base : " + allUsers.size() + "</p>" +
                "<p>Email testé : " + testEmail + (alreadyExists ? " (Déjà présent)" : " (Ajouté !)") + "</p>";
    }
}