package com.github.sidd6p.store.repositories;

import com.github.sidd6p.store.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    // <User, Long> specifies that this repository manages User entities with Long as the ID type.
}
