package com.github.sidd6p.store.controllers;

import com.github.sidd6p.store.dtos.LoginRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ┌─────────────────────────────────────────────────────────────────────────┐
 * │                        AUTHENTICATION FLOW                             │
 * └─────────────────────────────────────────────────────────────────────────┘
 *
 * ┌─────────────────────────────────────────────────────────────────────────┐
 * │ SPRING BOOT STARTUP: How beans connect                                 │
 * └─────────────────────────────────────────────────────────────────────────┘
 *
 * PasswordConfig → PasswordEncoder (BCrypt)
 *       ↓
 * UserServices → implements UserDetailsService
 *       ↓
 * SecurityConfig → creates AuthenticationProvider (uses above 2)
 *       ↓
 * AuthenticationManager → gets ALL AuthenticationProviders (auto-wiring)
 *       ↓
 * AuthController → gets injected with AuthenticationManager
 *
 * ┌─────────────────────────────────────────────────────────────────────────┐
 * │ LOGIN REQUEST: What happens step by step                               │
 * └─────────────────────────────────────────────────────────────────────────┘
 *
 * Client → POST /auth/login {"email": "user@example.com", "password": "plain"}
 *    ↓
 * 1. AuthController.login() validates request
 *    ↓
 * 2. Creates UsernamePasswordAuthenticationToken(email, password)
 *    ↓
 * 3. authenticationManager.authenticate(token)
 *    ↓
 * 4. AuthenticationManager finds YOUR DaoAuthenticationProvider
 *    ↓
 * 5. DaoAuthenticationProvider calls YOUR UserServices.loadUserByUsername(email)
 *    ↓
 * 6. UserServices calls UserRepository.findByEmail() → database query
 *    ↓
 * 7. Returns UserDetails with hashed password from database
 *    ↓
 * 8. DaoAuthenticationProvider calls YOUR PasswordEncoder.matches(plainPassword, hashedPassword)
 *    ↓
 * 9. BCrypt hashes the plain password and compares with stored hash
 *    ↓
 * 10. SUCCESS → return 200 OK  |  FAILURE → BadCredentialsException → 401 Unauthorized
 *
 * ┌─────────────────────────────────────────────────────────────────────────┐
 * │ KEY POINT: Why YOUR beans are used                                     │
 * └─────────────────────────────────────────────────────────────────────────┘
 *
 * • Spring Boot automatically discovers your @Bean methods
 * • AuthenticationManager gets ALL AuthenticationProvider beans (yours included)
 * • Your DaoAuthenticationProvider uses YOUR UserServices and PasswordEncoder
 * • @AllArgsConstructor + dependency injection connects everything
 *
 * Result: One line of code triggers your entire authentication pipeline!
 */
@RestController
@AllArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    // This contains YOUR AuthenticationProvider from SecurityConfig!
    private final AuthenticationManager authenticationManager;

    @PostMapping("/login")
    public ResponseEntity<Void> login(@Valid @RequestBody LoginRequest request) {
        // This one line triggers the entire flow above:
        // Token creation → Your AuthProvider → Your UserServices → Your PasswordEncoder
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        return ResponseEntity.ok().build();
    }
}
