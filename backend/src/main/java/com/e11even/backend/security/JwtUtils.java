package com.e11even.backend.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtils {

    
    private final String jwtSecret = "CeciEstUneCleSecreteTresLonguePourE11evenMeetup2026!*";
    
    // Durée de validité du token : 1 jour 
    private final int jwtExpirationMs = 86400000;

    // Convertit la chaîne de caractères en vraie clé cryptographique
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    // 1. Méthode pour FABRIQUER le token lors de la connexion
    public String generateJwtToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // 2. Méthode pour LIRE le token et retrouver l'email de l'utilisateur
    public String getEmailFromJwtToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // 3. Méthode pour VÉRIFIER que le token n'est pas faux ou expiré
    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(authToken);
            return true;
        } catch (Exception e) {
            System.err.println("Erreur JWT : " + e.getMessage());
        }
        return false;
    }
}