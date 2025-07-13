package com.github.sidd6p.store.services;

import com.github.sidd6p.store.entities.Product;
import com.github.sidd6p.store.repositories.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@AllArgsConstructor
public class ProductServices {
    private final ProductRepository productRepository;

    public void find(String name) {
        productRepository.findByNameContainingIgnoreCase(name).forEach(System.out::println);
    }

    public void findByIDBetweenOrderbyName(Integer startId, Integer endId) {
        productRepository.findByIDBetweenOrderbyName(startId, endId).forEach(System.out::println);
    }

    public void findProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        productRepository.findProductsByPriceRange(minPrice, maxPrice).forEach(System.out::println);
    }

    /**
     * Query by Example (QBE) - finds entities matching a probe object's field values.
     * Creates a probe with desired criteria, wraps in Example.of(), and queries.
     * Only non-null fields are used in the generated query.
     */
    public void findByExample() {
        // Create probe with search criteria
        Product probe = new Product();
        probe.setName("Laptop");
        probe.setPrice(new BigDecimal("999.99"));

        // Find all products matching the probe
        Example<Product> example = Example.of(probe);

        // Execute the query - finds all products matching the probe's criteria
        productRepository.findAll(example).forEach(System.out::println);
    }

    public void fetchSortedProductsByPrice() {
        var sort = Sort.by(Sort.Direction.ASC, "price").and(Sort.by(Sort.Direction.DESC, "name"));
        productRepository.findAll(sort).forEach(System.out::println);

    }
}
