package com.github.sidd6p.store.services;

import com.github.sidd6p.store.entities.User;
import com.github.sidd6p.store.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthService {
    private final UserRepository userRepository;

    public User getCurrentUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userID = (Long) authentication.getPrincipal();
        return userRepository.findById(userID).orElse(null);
    }
}
