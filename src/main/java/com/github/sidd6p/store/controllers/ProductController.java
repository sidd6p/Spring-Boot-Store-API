package com.github.sidd6p.store.controllers;


import com.github.sidd6p.store.dtos.ProductDto;
import com.github.sidd6p.store.dtos.RegisterProductRequest;
import com.github.sidd6p.store.mappers.ProductMapper;
import com.github.sidd6p.store.repositories.ProductRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

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

    @PostMapping()
    public ResponseEntity<ProductDto> createProduct(@RequestBody RegisterProductRequest registerProductRequest, UriComponentsBuilder uriBuilder) {
       log.info("Creating product with details: {}", registerProductRequest);
        var product = productMapper.toEntity(registerProductRequest);
        productRepository.save(product);
        var uri = uriBuilder.path("/products/{id}")
                .buildAndExpand(product.getId())
                .toUri();
        var productDto = productMapper.toDto(product);
        return ResponseEntity.created(uri).body(productDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ProductDto> deleteProductById(@PathVariable("id") Integer id) {
        log.info("Deleting product by id: {}", id);
        var product = productRepository.findById(id).orElse(null);
        if (product == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            productRepository.delete(product);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDto> updateProduct(@PathVariable("id") Integer id, @RequestBody RegisterProductRequest registerProductRequest) {
        log.info("Updating product with id: {} with details: {}", id, registerProductRequest);
        var product = productRepository.findById(id).orElse(null);
        if (product == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            // Update name and price directly
            if (registerProductRequest.getName() != null) {
                product.setName(registerProductRequest.getName());
            }
            if (registerProductRequest.getPrice() != null) {
                product.setPrice(registerProductRequest.getPrice());
            }

            // Only update category if category_id is provided and valid
            if (registerProductRequest.getCategory_id() != null) {
                var updatedProduct = productMapper.toEntity(registerProductRequest);
                if (updatedProduct.getCategory() != null) {
                    product.setCategory(updatedProduct.getCategory());
                }  else {
                    log.info("Invalid category_id provided for product update: {}", registerProductRequest.getCategory_id());
                     return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                 }
            }

            productRepository.save(product);
            return ResponseEntity.ok(productMapper.toDto(product));
        }
    }

}
