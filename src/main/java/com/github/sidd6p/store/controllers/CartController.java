package com.github.sidd6p.store.controllers;


import com.github.sidd6p.store.dtos.CartDto;
import com.github.sidd6p.store.entities.Cart;
import com.github.sidd6p.store.mappers.CartMapper;
import com.github.sidd6p.store.repositories.CartRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@AllArgsConstructor
@RestController
@Slf4j
@RequestMapping("/carts")
public class CartController {
    private final CartRepository cartRepository;
    private final CartMapper cartMapper;

    @PostMapping()
    public ResponseEntity<CartDto> createCart(UriComponentsBuilder uriComponentsBuilder) {
        log.info("Creating a new cart");

        var cart = new Cart();
        cartRepository.save(cart);
        var uri = uriComponentsBuilder.path("/users/{id}").buildAndExpand(cart.getId()).toUri();

        return ResponseEntity.created(uri).body(cartMapper.toDto(cart));
    }
}
