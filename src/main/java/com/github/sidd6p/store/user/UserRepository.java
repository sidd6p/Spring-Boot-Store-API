package com.github.sidd6p.store.user;

public interface UserRepository {
    void save(User user);
    User findByEmail(String email);
}
