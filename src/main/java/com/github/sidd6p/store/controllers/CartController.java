package com.github.sidd6p.store.controllers;

import com.github.sidd6p.store.dtos.AddItemToCartRequest;
import com.github.sidd6p.store.dtos.AddItemToCartResponse;
import com.github.sidd6p.store.dtos.CartDto;
import com.github.sidd6p.store.entities.Cart;
import com.github.sidd6p.store.entities.CartItem;
import com.github.sidd6p.store.mappers.AddItemToCartResponseMapper;
import com.github.sidd6p.store.mappers.CartMapper;
import com.github.sidd6p.store.repositories.CartRepository;
import com.github.sidd6p.store.repositories.ProductRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.UUID;

@AllArgsConstructor
@RestController
@Slf4j
@RequestMapping("/carts")
public class CartController {
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final CartMapper cartMapper;
    private final AddItemToCartResponseMapper addItemToCartResponseMapper;
    private final EntityManager entityManager;


    @GetMapping("/{cartID}")
    public ResponseEntity<CartDto> getCartById(@PathVariable UUID cartID) {
        log.info("Fetching cart with ID: {}", cartID);
        var cart = cartRepository.findById(cartID).orElse(null);
        if (cart == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(cartMapper.toDto(cart));
    }


    @PostMapping()
    /**
     * @Transactional is REQUIRED here because we're using EntityManager directly instead of repository.save()
     *
     * Without @Transactional: All EntityManager operations below would throw TransactionRequiredException
     *
     * Makes this function act as ATOMIC SQL Transaction - either ALL operations succeed or ALL fail (rollback)
     *
     * Why we use this 3-step approach:
     * 1. persist(cart) - Makes JPA track this new entity (doesn't hit DB yet)
     * 2. flush() - Forces immediate SQL execution to get auto-generated ID and timestamps
     * 3. refresh(cart) - Reloads entity from DB to populate generated fields (id, dateCreated, etc.)
     *
     * Alternative: cartRepository.save(cart) would handle all this automatically but we need
     * immediate access to generated values for logging and response building
     */
    @Transactional
    public ResponseEntity<CartDto> createCart(UriComponentsBuilder uriComponentsBuilder) {
        log.info("Creating a new cart");

        var cart = new Cart();

        entityManager.persist(cart);
        entityManager.flush();
        entityManager.refresh(cart);

        var cart_id = cart.getId();

        var uri = uriComponentsBuilder.path("/carts/{id}").buildAndExpand(cart.getId()).toUri();
        return ResponseEntity.created(uri).body(cartMapper.toDto(cart));
    }

    @PostMapping("/{cartID}/items")
    @Transactional
    public ResponseEntity<AddItemToCartResponse> addToCart(@PathVariable UUID cartID, @RequestBody AddItemToCartRequest addItemToCartRequest) {
        log.info("Adding item to cart with ID: {}", cartID);

        var cart = cartRepository.findById(cartID).orElse(null);
        if (cart == null) {
            return ResponseEntity.notFound().build();
        }

        var product = productRepository.findById(addItemToCartRequest.getProductId()).orElse(null);
        if (product == null) {
            return ResponseEntity.badRequest().build();
        }

        var cartItem = cart.getCartItems().stream()
                .filter(item -> item.getProduct().getId().equals(product.getId()))
                .findFirst()
                .orElse(null);

        if (cartItem != null) {
            cartItem.setQuantity(cartItem.getQuantity() + 1);
        } else {
           cartItem = new CartItem();
           cartItem.setQuantity(1);
           cartItem.setProduct(product);
           cartItem.setCart(cart);
           cart.getCartItems().add(cartItem);
        }

        cartRepository.save(cart);
        entityManager.flush();
        entityManager.refresh(cart);

        // Find the persisted cartItem to get its generated ID
        var persistedCartItem = cart.getCartItems().stream()
                .filter(item -> item.getProduct().getId().equals(product.getId()))
                .findFirst()
                .orElse(cartItem);

        // Use mapper to create the response - much simpler!
        return ResponseEntity.ok(addItemToCartResponseMapper.toResponse(persistedCartItem));
    }

}
