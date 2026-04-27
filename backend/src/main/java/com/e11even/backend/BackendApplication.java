package com.e11even.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean; // 👈 AJOUTE ÇA
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; // 👈 AJOUTE ÇA
import org.springframework.security.crypto.password.PasswordEncoder; // 👈 AJOUTE ÇA

@SpringBootApplication
public class BackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}