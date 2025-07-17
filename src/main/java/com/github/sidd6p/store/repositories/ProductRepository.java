package com.github.sidd6p.store.repositories;

import com.github.sidd6p.store.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Integer> {

    // This method uses Spring Data JPA's query derivation mechanism.
    // The method name "findByNameContainingIgnoreCase" tells Spring to generate a query that finds all products
    // where the 'name' field contains the given string (case-insensitive).
    // Naming is important: 'findBy' + field + 'Containing' + 'IgnoreCase' triggers the correct query generation.
    List<Product> findByNameContainingIgnoreCase(String name);

    // Using JPQL instead of native SQL to avoid table resolution issues
    @Query("SELECT p FROM Product p WHERE p.id BETWEEN :min AND :max ORDER BY p.name")
    List<Product> findByIDBetweenOrderbyName(@Param("min") Integer startId, @Param("max") Integer endId);

    // Using JPQL for price range query instead of stored procedure
    @Query("SELECT p FROM Product p WHERE p.price BETWEEN :minPrice AND :maxPrice")
    List<Product> findProductsByPriceRange(@Param("minPrice") BigDecimal minPrice, @Param("maxPrice") BigDecimal maxPrice);

    // Original method for backward compatibility
    List<Product> findByCategoryName(String categoryName);

    // Override findAll to eagerly load categories and prevent N+1 queries
    @Query("SELECT p FROM Product p JOIN FETCH p.category")
    @Override
    List<Product> findAll();

    // Method to find products by category name with eager loading
    @Query("SELECT p FROM Product p JOIN FETCH p.category c WHERE c.name = :categoryName")
    List<Product> findByCategoryNameWithCategory(@Param("categoryName") String categoryName);

}