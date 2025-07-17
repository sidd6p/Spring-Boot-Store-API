package com.github.sidd6p.store.controllers;


import com.github.sidd6p.store.dtos.ProductDto;
import com.github.sidd6p.store.mappers.ProductMapper;
import com.github.sidd6p.store.repositories.ProductRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
@AllArgsConstructor
@Slf4j
public class ProductController {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;


    @GetMapping()
    public List<ProductDto> getAllProducts(@RequestParam(required = false, name = "category") String category) {
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

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable("id") Integer id) {
        log.info("Fetching product by id: {}", id);
        return productRepository.findById(id)
                .map(productMapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
