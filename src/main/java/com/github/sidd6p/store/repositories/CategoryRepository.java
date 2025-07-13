package com.github.sidd6p.store.repositories;

import com.github.sidd6p.store.entities.Category;
import org.springframework.data.repository.CrudRepository;

public interface CategoryRepository extends CrudRepository<Category, Integer> {
}