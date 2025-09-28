package com.github.sidd6p.store.repositories;

import com.github.sidd6p.store.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);
    // <User, Long> specifies that this repository manages User entities with Long as the ID type.
}
