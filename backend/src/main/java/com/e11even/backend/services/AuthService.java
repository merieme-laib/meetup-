package com.e11even.backend.services;

import com.e11even.backend.models.User;
import com.e11even.backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    public User register(User user) {
        // On pourrait ajouter ici le hachage du mot de passe plus tard !
        return userRepository.save(user);
    }
}