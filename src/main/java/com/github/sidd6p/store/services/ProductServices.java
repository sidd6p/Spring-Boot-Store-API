package com.github.sidd6p.store.services;

import com.github.sidd6p.store.dtos.ProductDto;
import com.github.sidd6p.store.dtos.RegisterProductRequest;
import com.github.sidd6p.store.entities.Category;
import com.github.sidd6p.store.entities.Product;
import com.github.sidd6p.store.mappers.ProductMapper;
import com.github.sidd6p.store.repositories.ProductRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class ProductServices {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public List<ProductDto> getAllProducts(String category) {
        if (category != null && !category.isEmpty()) {
            log.info("Fetching products for category: {}", category);
            return productRepository.findByCategoryNameWithCategory(category).stream()
                    .map(productMapper::toDto)
                    .toList();
        } else {
            log.info("Fetching all products");
            return productRepository.findAll().stream()
                    .map(productMapper::toDto)
                    .toList();
        }
    }

    public Optional<ProductDto> getProductById(Integer id) {
        log.info("Fetching product by id: {}", id);
        return productRepository.findById(id)
                .map(productMapper::toDto);
    }

    public ProductDto createProduct(RegisterProductRequest registerProductRequest) {
        log.info("Creating product with details: {}", registerProductRequest);
        var product = productMapper.toEntity(registerProductRequest);
        productRepository.save(product);
        return productMapper.toDto(product);
    }

    public boolean deleteProductById(Integer id) {
        log.info("Deleting product by id: {}", id);
        return productRepository.findById(id)
                .map(product -> {
                    productRepository.delete(product);
                    return true;
                })
                .orElse(false);
    }

    public Optional<ProductDto> updateProduct(Integer id, RegisterProductRequest registerProductRequest) {
        log.info("Updating product with id: {} with details: {}", id, registerProductRequest);

        return productRepository.findById(id)
                .map(product -> {
                    // Handle category update if category_id is provided
                    Category categoryToSet = null;
                    if (registerProductRequest.getCategory_id() != null) {
                        var updatedProduct = productMapper.toEntity(registerProductRequest);
                        if (updatedProduct.getCategory() == null) {
                            log.info("Invalid category_id provided for product update: {}", registerProductRequest.getCategory_id());
                            throw new IllegalArgumentException("Invalid category_id provided");
                        }
                        categoryToSet = updatedProduct.getCategory();
                    }

                    // Use Product's business logic method
                    product.updateFromRequest(
                        registerProductRequest.getName(),
                        registerProductRequest.getPrice(),
                        categoryToSet
                    );

                    productRepository.save(product);
                    return productMapper.toDto(product);
                });
    }

    // Legacy methods for demonstration purposes - can be kept or moved to a separate demo service
    public void find(String name) {
        productRepository.findByNameContainingIgnoreCase(name).forEach(System.out::println);
    }

    public void findByIDBetweenOrderbyName(Integer startId, Integer endId) {
        productRepository.findByIDBetweenOrderbyName(startId, endId).forEach(System.out::println);
    }

    public void findProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        productRepository.findProductsByPriceRange(minPrice, maxPrice).forEach(System.out::println);
    }

    public void findByExample() {
        Product probe = new Product();
        probe.setName("Laptop");
        probe.setPrice(new BigDecimal("999.99"));

        Example<Product> example = Example.of(probe);
        productRepository.findAll(example).forEach(System.out::println);
    }

    public void fetchSortedProductsByPrice() {
        var sort = Sort.by(Sort.Direction.ASC, "price").and(Sort.by(Sort.Direction.DESC, "name"));
        List<Product> products = productRepository.findAll(sort);
        products.forEach(System.out::println);
    }

    public void fetchPaginatedProducts(int page, int size) {
        var pageable = PageRequest.of(page, size);
        Page<Product> productPage = productRepository.findAll(pageable);
        System.out.println("Total Pages: " + productPage.getTotalPages());
        System.out.println("Total Elements: " + productPage.getTotalElements());
        System.out.println("Current Page: " + productPage.getNumber());
        System.out.println("Page Size: " + productPage.getSize());
        productPage.getContent().forEach(System.out::println);
    }
}
