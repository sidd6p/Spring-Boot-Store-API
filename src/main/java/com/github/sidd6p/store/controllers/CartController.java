package com.github.sidd6p.store.controllers;

import com.github.sidd6p.store.dtos.AddItemToCartRequest;
import com.github.sidd6p.store.dtos.AddItemToCartResponse;
import com.github.sidd6p.store.dtos.CartDto;
import com.github.sidd6p.store.dtos.UpdateCartItemRequest;
import com.github.sidd6p.store.services.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Cart Management", description = "APIs for managing shopping carts")
public class CartController {
    private final CartService cartService;

    @GetMapping("/{cartID}")
    @Operation(summary = "Get cart by ID", description = "Retrieve a shopping cart by its ID.")
    public ResponseEntity<CartDto> getCartById(@PathVariable UUID cartID) {
        return cartService.getCartById(cartID)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping()
    @Operation(summary = "Create new cart", description = "Create a new empty shopping cart.")
    public ResponseEntity<CartDto> createCart(UriComponentsBuilder uriComponentsBuilder) {
        var cartDto = cartService.createCart();
        var uri = uriComponentsBuilder.path("/carts/{id}")
                .buildAndExpand(cartDto.getId())
                .toUri();
        return ResponseEntity.created(uri).body(cartDto);
    }

    @PostMapping("/{cartID}/items")
    @Operation(summary = "Add item to cart", description = "Add a product to the shopping cart.")
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
    @Operation(summary = "Update cart item quantity", description = "Update the quantity of a specific item in the cart.")
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
    @Operation(summary = "Remove item from cart", description = "Remove a specific item from the shopping cart.")
    public ResponseEntity<Void> removeCartItem(@PathVariable UUID cartID, @PathVariable Integer productId) {
        if (cartService.removeCartItem(cartID, productId)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{cartID}/items")
    @Operation(summary = "Clear cart", description = "Remove all items from the shopping cart.")
    public ResponseEntity<Void> clearCart(@PathVariable UUID cartID) {
        if (cartService.clearCart(cartID)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
