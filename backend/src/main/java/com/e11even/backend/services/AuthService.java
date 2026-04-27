package com.e11even.backend.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.e11even.backend.models.User;
import com.e11even.backend.repositories.UserRepository;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Enregistre un nouvel utilisateur en hachant son mot de passe.
     */
    public User register(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    /**
     * Authentifie un utilisateur en comparant le mot de passe brut
     * avec le hachage stocké en base de données.
     */
    public User login(String email, String password) {
        // 1. On cherche l'utilisateur par son email
        Optional<User> userOpt = userRepository.findByEmail(email);

        // 2. On vérifie s'il existe et si le mot de passe (BCrypt) correspond
        if (userOpt.isPresent() && passwordEncoder.matches(password, userOpt.get().getPassword())) {
            User user = userOpt.get();

            // 3. On vérifie que le compte n'est pas marqué comme supprimé
            if (user.getIsDeleted() != null && user.getIsDeleted()) {
                throw new RuntimeException("Ce compte a été supprimé");
            }

            return user;
        }

        // 4. Si l'utilisateur n'existe pas ou le mot de passe est faux
        throw new RuntimeException("Email ou mot de passe incorrect");
    }
}