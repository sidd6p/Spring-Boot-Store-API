package com.github.sidd6p.store.user;

import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class InMemoryUserRepository implements UserRepository {
    private final Map<String, User> userStore = new HashMap<>();

    @Override
    public void save(User user) {
        userStore.put(user.getEmail(), user);
    }

    @Override
    public User findByEmail(String email) {
        return userStore.getOrDefault(email, null);
    }
}
