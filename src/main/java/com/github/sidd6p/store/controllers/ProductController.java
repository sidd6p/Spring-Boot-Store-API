package com.github.sidd6p.store.controllers;


import com.github.sidd6p.store.dtos.ProductDto;
import com.github.sidd6p.store.dtos.RegisterProductRequest;
import com.github.sidd6p.store.services.ProductServices;
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
    private final ProductServices productServices;

    @GetMapping()
    public List<ProductDto> getAllProducts(@RequestParam(required = false, name = "category") String category) {
        return productServices.getAllProducts(category);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable("id") Integer id) {
        return productServices.getProductById(id)
                .map(ResponseEntity::ok)
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping()
    public ResponseEntity<ProductDto> createProduct(@RequestBody RegisterProductRequest registerProductRequest,
                                                   UriComponentsBuilder uriBuilder) {
        var productDto = productServices.createProduct(registerProductRequest);
        var uri = uriBuilder.path("/products/{id}")
                .buildAndExpand(productDto.getId())
                .toUri();
        return ResponseEntity.created(uri).body(productDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProductById(@PathVariable("id") Integer id) {
        if (productServices.deleteProductById(id)) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDto> updateProduct(@PathVariable("id") Integer id,
                                                   @RequestBody RegisterProductRequest registerProductRequest) {
        try {
            return productServices.updateProduct(id, registerProductRequest)
                    .map(ResponseEntity::ok)
                    .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
