package com.github.sidd6p.store.services;

import com.github.sidd6p.store.entities.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JwtService {
    @Value("${spring.jwt.secret}")
    private String secret;
    public String generateAccessToken(User user) {
        final long expirationTime = 600; // 1 day in milliseconds
        return getToken(user, expirationTime);
    }

    public String generateRefreshToken(User user) {
        final long expirationTime = 864000; // 1 day in milliseconds
        return getToken(user, expirationTime);
    }


    private String getToken(User user, long expirationTime) {
        return Jwts.builder().subject(user.getId().toString())
                .claim("email", user.getEmail())
                .claim("name", user.getName())
                .issuedAt(new Date()).expiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(Keys.hmacShaKeyFor(secret.getBytes()))
                .compact();
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(secret.getBytes()))
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
