package com.e11even.backend.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.e11even.backend.models.User;
import com.e11even.backend.repositories.UserRepository;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    public User register(User user) {
        return userRepository.save(user);
    }

    // public User login(String email, String password) {
    //     // 1. On cherche l'utilisateur par son email
    //     Optional<User> userOpt = userRepository.findByEmail(email);

    //     // 2. On vérifie s'il existe et si le mot de passe correspond
    //     if (userOpt.isPresent() && userOpt.get().getPassword().equals(password)) {
    //         return userOpt.get();
    //     }

    //     // 3. Si ça rate, on lance une erreur
    //     throw new RuntimeException("Email ou mot de passe incorrect");
    // }

    public User login(String email, String password) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent() && userOpt.get().getPassword().equals(password)) {
            User user = userOpt.get();
            if (user.getIsDeleted()) {
                throw new RuntimeException("Ce compte a été supprimé");
            }
            return user;
        }
        throw new RuntimeException("Email ou mot de passe incorrect");
    }
}