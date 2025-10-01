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

    public Jwt generateAccessToken(User user) {
        return getToken(user, jwtConfig.getAccessTokenExpiration() * 1000L); // Convert seconds to milliseconds
    }

    public Jwt generateRefreshToken(User user) {
        return getToken(user, jwtConfig.getRefreshTokenExpiration() * 1000L); // Convert seconds to milliseconds
    }

    private Jwt getToken(User user, long expirationTimeInMillis) {
        var claims = Jwts.claims().subject(user.getId().toString())
                .add("email", user.getEmail())
                .add("name", user.getName())
                .add("role", user.getRole().name())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationTimeInMillis))
                .build();
        return new Jwt(claims, jwtConfig.getSecretKey());
    }

    public Jwt parseToken(String token) {
        try {
            var claims = getClaims(token);
            return new Jwt(claims, jwtConfig.getSecretKey());
        } catch (Exception e) {
            return null;
        }

    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(jwtConfig.getSecretKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
