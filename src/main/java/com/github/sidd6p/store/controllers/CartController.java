package com.github.sidd6p.store.controllers;

import com.github.sidd6p.store.dtos.AddItemToCartRequest;
import com.github.sidd6p.store.dtos.AddItemToCartResponse;
import com.github.sidd6p.store.dtos.CartDto;
import com.github.sidd6p.store.dtos.UpdateCartItemRequest;
import com.github.sidd6p.store.services.CartService;
import jakarta.validation.Valid;
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
    private final CartService cartService;

    @GetMapping("/{cartID}")
    public ResponseEntity<CartDto> getCartById(@PathVariable UUID cartID) {
        return cartService.getCartById(cartID)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping()
    public ResponseEntity<CartDto> createCart(UriComponentsBuilder uriComponentsBuilder) {
        var cartDto = cartService.createCart();
        var uri = uriComponentsBuilder.path("/carts/{id}")
                .buildAndExpand(cartDto.getId())
                .toUri();
        return ResponseEntity.created(uri).body(cartDto);
    }

    @PostMapping("/{cartID}/items")
    public ResponseEntity<AddItemToCartResponse> addToCart(@PathVariable UUID cartID,
                                                          @RequestBody AddItemToCartRequest addItemToCartRequest) {
        try {
            return cartService.addToCart(cartID, addItemToCartRequest)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(409).build();
        }
    }

    @PutMapping("/{cartID}/items/{productId}")
    public ResponseEntity<CartDto> updateCartItemQuantity(@PathVariable UUID cartID,
                                                         @PathVariable Integer productId,
                                                         @Valid @RequestBody UpdateCartItemRequest request) {
        try {
            return cartService.updateCartItemQuantity(cartID, productId, request)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{cartID}/items/{productId}")
    public ResponseEntity<Void> removeCartItem(@PathVariable UUID cartID, @PathVariable Integer productId) {
        if (cartService.removeCartItem(cartID, productId)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{cartID}/items")
    public ResponseEntity<Void> clearCart(@PathVariable UUID cartID) {
        if (cartService.clearCart(cartID)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
