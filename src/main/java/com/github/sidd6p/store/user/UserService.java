package com.github.sidd6p.store.user;

import com.github.sidd6p.store.notification.NotificationManager;
import com.github.sidd6p.store.notification.NotificationService;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final NotificationManager notificationManager;

    public UserService(UserRepository userRepository, NotificationManager notificationManager) {
        this.userRepository = userRepository;
        this.notificationManager = notificationManager;
        System.out.println("UserService created");
    }

    public void registerUser(User username) {
        if (userRepository.findByEmail(username.getEmail()) != null) {
            throw new IllegalArgumentException("Username already exists");
        }

        userRepository.save(username);
        notificationManager.notify("Welcome " + username + "! You have been registered successfully.", username.getEmail());
        System.out.println("User " + username + " registered successfully.");
    }
}
