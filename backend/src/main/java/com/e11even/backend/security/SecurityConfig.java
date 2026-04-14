package com.e11even.backend.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // 1. ON ACTIVE CORS ICI !
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            // 2. On désactive CSRF
            .csrf(csrf -> csrf.disable())
            
            // 3. Mode Stateless pour le JWT
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            // 4. Règles des routes
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .anyRequest().authenticated()
            );

        return http.build();
    }

    // --- LA NOUVELLE RÈGLE CORS POUR ACCEPTER TON FRONTEND ---
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // On autorise explicitement ton frontend local
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173"));
        
        // On autorise les méthodes (POST pour le login/register, GET, etc.)
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        
        // On autorise tous les en-têtes (très important pour que le Token passe)
        configuration.setAllowedHeaders(Arrays.asList("*"));
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // On applique cette règle à toutes les URL de ton API (/**)
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}