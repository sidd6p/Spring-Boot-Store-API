package com.github.sidd6p.store.repositories;

import com.github.sidd6p.store.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Integer> {

    // This method uses Spring Data JPA's query derivation mechanism.
    // The method name "findByNameContainingIgnoreCase" tells Spring to generate a query that finds all products
    // where the 'name' field contains the given string (case-insensitive).
    // Naming is important: 'findBy' + field + 'Containing' + 'IgnoreCase' triggers the correct query generation.
    List<Product> findByNameContainingIgnoreCase(String name);

    @Query(value = "SELECT * FROM products p WHERE p.id BETWEEN :min AND :max ORDER BY p.name", nativeQuery = true)
    List<Product> findByIDBetweenOrderbyName(@Param("min") Integer startId, @Param("max") Integer endId);

    @Query(value = "CALL FindProductsByPrice(:minPrice, :maxPrice)", nativeQuery = true)
    List<Product> findProductsByPriceRange(@Param("minPrice") BigDecimal minPrice, @Param("maxPrice") BigDecimal maxPrice);

}