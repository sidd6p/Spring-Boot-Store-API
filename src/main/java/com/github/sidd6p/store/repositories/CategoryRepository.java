package com.github.sidd6p.store.repositories;

import com.github.sidd6p.store.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}