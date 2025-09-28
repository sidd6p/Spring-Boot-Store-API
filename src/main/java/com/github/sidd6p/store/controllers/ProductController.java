package com.github.sidd6p.store.controllers;


import com.github.sidd6p.store.dtos.ProductDto;
import com.github.sidd6p.store.dtos.RegisterProductRequest;
import com.github.sidd6p.store.services.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Product", description = "Product management APIs")
public class ProductController {
    private final ProductService productService;

    @GetMapping()
    @Operation(summary = "Get all products", description = "Retrieve a list of all products, optionally filtered by category.")
    public List<ProductDto> getAllProducts(@RequestParam(required = false, name = "category") String category) {
        return productService.getAllProducts(category);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product by ID", description = "Retrieve a specific product by its ID.")
    public ResponseEntity<ProductDto> getProductById(@PathVariable("id") Integer id) {
        return productService.getProductById(id)
                .map(ResponseEntity::ok)
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping()
    @Operation(summary = "Create new product", description = "Register a new product in the system.")
    public ResponseEntity<ProductDto> createProduct(@RequestBody RegisterProductRequest registerProductRequest,
                                                   UriComponentsBuilder uriBuilder) {
        var productDto = productService.createProduct(registerProductRequest);
        var uri = uriBuilder.path("/products/{id}")
                .buildAndExpand(productDto.getId())
                .toUri();
        return ResponseEntity.created(uri).body(productDto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete product", description = "Remove a product from the system.")
    public ResponseEntity<Void> deleteProductById(@PathVariable("id") Integer id) {
        if (productService.deleteProductById(id)) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update product", description = "Update an existing product's information.")
    public ResponseEntity<ProductDto> updateProduct(@PathVariable("id") Integer id,
                                                   @RequestBody RegisterProductRequest registerProductRequest) {
        try {
            return productService.updateProduct(id, registerProductRequest)
                    .map(ResponseEntity::ok)
                    .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
