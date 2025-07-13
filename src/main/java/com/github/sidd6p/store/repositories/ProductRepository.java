package com.github.sidd6p.store.repositories;

import com.github.sidd6p.store.entities.Product;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ProductRepository extends CrudRepository<Product, Long> {

    // This method uses Spring Data JPA's query derivation mechanism.
    // The method name "findByNameContainingIgnoreCase" tells Spring to generate a query that finds all products
    // where the 'name' field contains the given string (case-insensitive).
    // Naming is important: 'findBy' + field + 'Containing' + 'IgnoreCase' triggers the correct query generation.
    List<Product> findByNameContainingIgnoreCase(String name);

}