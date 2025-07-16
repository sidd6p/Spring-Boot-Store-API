package com.github.sidd6p.store.controllers;

import com.github.sidd6p.store.entities.User;
import com.github.sidd6p.store.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
public class UserController {
    private final UserRepository userRepository;

    @GetMapping("/v1/users")
    public Iterable<User> getAllUsers() {
        var users =  userRepository.findAll();
        System.out.println(users);
        return users;
    }
}
