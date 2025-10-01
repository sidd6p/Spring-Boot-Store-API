package com.github.sidd6p.store.controllers;

import com.github.sidd6p.store.dtos.JwtResponse;
import com.github.sidd6p.store.dtos.LoginRequest;
import com.github.sidd6p.store.dtos.UserDto;
import com.github.sidd6p.store.mappers.UserMapper;
import com.github.sidd6p.store.repositories.UserRepository;
import com.github.sidd6p.store.services.JwtService;
import com.github.sidd6p.store.services.UserService;
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
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody LoginRequest request) {
        // This one line triggers the entire flow above:
        // Token creation → Your AuthProvider → Your UserServices → Your PasswordEncoder
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        var user = userRepository.findByEmail(request.email).orElseThrow();
        var token = jwtService.generateToken(user);

        return ResponseEntity.ok(new JwtResponse(token));
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
