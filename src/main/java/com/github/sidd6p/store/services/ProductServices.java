package com.github.sidd6p.store.services;

import com.github.sidd6p.store.entities.Product;
import com.github.sidd6p.store.repositories.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ProductServices {
    private final ProductRepository productRepository;

    public void find(String name) {
        productRepository.findByNameContainingIgnoreCase(name).forEach(System.out::println);
    }

}
