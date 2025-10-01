package com.github.sidd6p.store.services;

import com.github.sidd6p.store.config.JwtConfig;
import com.github.sidd6p.store.entities.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@AllArgsConstructor
public class JwtService {
    private final JwtConfig jwtConfig;

    public String generateAccessToken(User user) {
        return getToken(user, jwtConfig.getAccessTokenExpiration() * 1000L); // Convert seconds to milliseconds
    }

    public String generateRefreshToken(User user) {
        return getToken(user, jwtConfig.getRefreshTokenExpiration() * 1000L); // Convert seconds to milliseconds
    }

    private String getToken(User user, long expirationTimeInMillis) {
        return Jwts.builder().subject(user.getId().toString())
                .claim("email", user.getEmail())
                .claim("name", user.getName())
                .claim("role", user.getRole().name())
                .issuedAt(new Date()).expiration(new Date(System.currentTimeMillis() + expirationTimeInMillis))
                .signWith(jwtConfig.getSecretKey())
                .compact();
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(jwtConfig.getSecretKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean validateToken(String token) {
        try {
            getClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Long getUserIdFromToken(String token) {
        return Long.valueOf(getClaims(token).getSubject());
    }
}
