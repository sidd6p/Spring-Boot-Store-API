package com.github.sidd6p.store.controllers;

import com.github.sidd6p.store.config.JwtConfig;
import com.github.sidd6p.store.dtos.JwtResponse;
import com.github.sidd6p.store.dtos.LoginRequest;
import com.github.sidd6p.store.dtos.UserDto;
import com.github.sidd6p.store.mappers.UserMapper;
import com.github.sidd6p.store.repositories.UserRepository;
import com.github.sidd6p.store.services.JwtService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * ┌─────────────────────────────────────────────────────────────────────────┐
 * │                        AUTHENTICATION FLOW                             │
 * └─────────────────────────────────────────────────────────────────────────┘
 * <p>
 * ┌─────────────────────────────────────────────────────────────────────────┐
 * │ SPRING BOOT STARTUP: How beans connect                                 │
 * └─────────────────────────────────────────────────────────────────────────┘
 * <p>
 * PasswordConfig → PasswordEncoder (BCrypt)
 * ↓
 * UserServices → implements UserDetailsService
 * ↓
 * SecurityConfig → creates AuthenticationProvider (uses above 2)
 * ↓
 * AuthenticationManager → gets ALL AuthenticationProviders (auto-wiring)
 * ↓
 * AuthController → gets injected with AuthenticationManager
 * <p>
 * ┌─────────────────────────────────────────────────────────────────────────┐
 * │ LOGIN REQUEST: What happens step by step                               │
 * └─────────────────────────────────────────────────────────────────────────┘
 * <p>
 * Client → POST /auth/login {"email": "user@example.com", "password": "plain"}
 * ↓
 * 1. AuthController.login() validates request
 * ↓
 * 2. Creates UsernamePasswordAuthenticationToken(email, password)
 * ↓
 * 3. authenticationManager.authenticate(token)
 * ↓
 * 4. AuthenticationManager finds YOUR DaoAuthenticationProvider
 * ↓
 * 5. DaoAuthenticationProvider calls YOUR UserServices.loadUserByUsername(email)
 * ↓
 * 6. UserServices calls UserRepository.findByEmail() → database query
 * ↓
 * 7. Returns UserDetails with hashed password from database
 * ↓
 * 8. DaoAuthenticationProvider calls YOUR PasswordEncoder.matches(plainPassword, hashedPassword)
 * ↓
 * 9. BCrypt hashes the plain password and compares with stored hash
 * ↓
 * 10. SUCCESS → return 200 OK  |  FAILURE → BadCredentialsException → 401 Unauthorized
 * <p>
 * ┌─────────────────────────────────────────────────────────────────────────┐
 * │ KEY POINT: Why YOUR beans are used                                     │
 * └─────────────────────────────────────────────────────────────────────────┘
 * <p>
 * • Spring Boot automatically discovers your @Bean methods
 * • AuthenticationManager gets ALL AuthenticationProvider beans (yours included)
 * • Your DaoAuthenticationProvider uses YOUR UserServices and PasswordEncoder
 * • @AllArgsConstructor + dependency injection connects everything
 * <p>
 * Result: One line of code triggers your entire authentication pipeline!
 */
@RestController
@AllArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    // This contains YOUR AuthenticationProvider from SecurityConfig!
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final JwtConfig jwtConfig;

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody LoginRequest request, HttpServletResponse response) {
        // This one line triggers the entire flow above:
        // Token creation → Your AuthProvider → Your UserServices → Your PasswordEncoder
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        var user = userRepository.findByEmail(request.email).orElseThrow();
        var accessToken = jwtService.generateAccessToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        var cookie = new Cookie("refreshToken", refreshToken);
        cookie.setHttpOnly(true);
        cookie.setPath("/auth/refresh");
        cookie.setMaxAge(jwtConfig.getRefreshTokenExpiration());
        cookie.setSecure(false); // in production this should be true, ensure cookies are sent over HTTPS
        response.addCookie(cookie);

        return ResponseEntity.ok(new JwtResponse(accessToken));
    }


    @PostMapping("/validate")
    public ResponseEntity<Boolean> validateToken(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (authHeader == null || authHeader.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(false);
        }

        if (!authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body(false);
        }

        String token = authHeader.substring(7);

        if (token.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(false);
        }

        boolean isValid = jwtService.validateToken(token);
        if (isValid) {
            return ResponseEntity.ok(true);
        } else {
            return ResponseEntity.status(401).body(false);
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<JwtResponse> refreshToken(@CookieValue(value = "refreshToken") String refreshToken) {
        if (refreshToken == null || refreshToken.trim().isEmpty()) {
            return ResponseEntity.status(401).build();
        }

        if (!jwtService.validateToken(refreshToken)) {
            return ResponseEntity.status(401).build();
        }

        Long userId = jwtService.getUserIdFromToken(refreshToken);
        var user = userRepository.findById(userId).orElseThrow();
        var newAccessToken = jwtService.generateAccessToken(user);

        return ResponseEntity.ok(new JwtResponse(newAccessToken));
    }


    @GetMapping("/me")
    public ResponseEntity<UserDto> getCurrentUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null) {
            return ResponseEntity.status(401).build();
        }
        Long userID = (Long) authentication.getPrincipal();
        var user = userRepository.findById(userID).orElse(null);
        if (user == null) {
            return ResponseEntity.status(401).build();
        }
        var userDto = userMapper.toDto(user);
        return ResponseEntity.ok(userDto);
    }
}
