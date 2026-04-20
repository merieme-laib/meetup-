package com.e11even.backend.security;

import org.springframework.beans.factory.annotation.Autowired; // <-- NOUVEAU
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter; // <-- NOUVEAU
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    //  (FILTRE JWT) 
    @Autowired
    private AuthTokenFilter authTokenFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // 1. ON ACTIVE CORS ICI 
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            // 2. On désactive CSRF
            .csrf(csrf -> csrf.disable())
            
            // 3. Mode Stateless pour le JWT
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            // 4. Règles des routes
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/events", "/api/events/**").permitAll()
                
                //LES DÉBLOQUEURS DE FAUX 403 
                .requestMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**").permitAll() // Laisse passer la vérification CORS du navigateur
                .requestMatchers("/error").permitAll() // Laisse passer les vrais messages d'erreur de Spring
                
                .anyRequest().authenticated()
            );

        // --- ON MET LE VIDEUR DEVANT LA PORTE ---
        http.addFilterBefore(authTokenFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // --- LA NOUVELLE RÈGLE CORS POUR ACCEPTER FRONTEND ---
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // On autorise explicitement ton frontend local
        configuration.setAllowedOrigins(Arrays.asList(
            "http://localhost:5173",       // Pour le développement local
            "http://192.168.75.120:5173",  // Pour la VM avec le port Vite
            "http://192.168.75.120",       // Pour la VM en HTTP simple
            "https://192.168.75.120"       // Pour la VM en HTTPS (sécurisé)
        ));
        
        // On autorise les méthodes (POST pour le login/register, GET, etc.)
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        
        // On autorise tous les en-têtes (très important pour que le Token passe)
        configuration.setAllowedHeaders(Arrays.asList("*"));
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // On applique cette règle à toutes les URL de l' API (/**)
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}