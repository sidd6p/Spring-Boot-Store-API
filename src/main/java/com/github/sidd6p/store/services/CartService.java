package com.github.sidd6p.store.services;

import com.github.sidd6p.store.dtos.AddItemToCartRequest;
import com.github.sidd6p.store.dtos.AddItemToCartResponse;
import com.github.sidd6p.store.dtos.CartDto;
import com.github.sidd6p.store.dtos.UpdateCartItemRequest;
import com.github.sidd6p.store.entities.Cart;
import com.github.sidd6p.store.mappers.AddItemToCartResponseMapper;
import com.github.sidd6p.store.mappers.CartMapper;
import com.github.sidd6p.store.repositories.CartRepository;
import com.github.sidd6p.store.repositories.ProductRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
public class CartService {
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final CartMapper cartMapper;
    private final AddItemToCartResponseMapper addItemToCartResponseMapper;
    private final EntityManager entityManager;

    public Optional<CartDto> getCartById(UUID cartId) {
        log.info("Fetching cart with ID: {}", cartId);
        return cartRepository.findById(cartId)
                .map(cartMapper::toDto);
    }


    @Transactional
    public CartDto createCart() {
        log.info("Creating a new cart");

        var cart = new Cart();
        entityManager.persist(cart);
        entityManager.flush();
        entityManager.refresh(cart);

        return cartMapper.toDto(cart);
    }

    @Transactional
    public Optional<AddItemToCartResponse> addToCart(UUID cartId, AddItemToCartRequest addItemToCartRequest) {
        log.info("Adding item to cart with ID: {}", cartId);

        var cart = cartRepository.findById(cartId).orElse(null);
        if (cart == null) {
            log.warn("Cart with ID {} not found", cartId);
            return Optional.empty();
        }

        var product = productRepository.findById(addItemToCartRequest.getProductId()).orElse(null);
        if (product == null) {
            log.warn("Product with ID {} not found", addItemToCartRequest.getProductId());
            throw new IllegalArgumentException("Product not found");
        }

        try {
            var cartItem = cart.addProduct(product);
            cartRepository.save(cart);
            entityManager.flush();
            entityManager.refresh(cart);

            // Find the persisted cartItem to get its generated ID
            var persistedCartItem = cart.findCartItemByProductId(product.getId())
                    .orElse(cartItem);

            return Optional.of(addItemToCartResponseMapper.toResponse(persistedCartItem));
        } catch (IllegalStateException e) {
            log.warn("Failed to add product to cart: {}", e.getMessage());
            throw new IllegalStateException("Product already exists in cart");
        }
    }

    @Transactional
    public Optional<CartDto> updateCartItemQuantity(UUID cartId, Integer productId, UpdateCartItemRequest request) {
        log.info("Updating quantity of product {} in cart {} to {}", productId, cartId, request.getQuantity());

        return cartRepository.findById(cartId)
                .map(cart -> {
                    if (!cart.updateProductQuantity(productId, request.getQuantity())) {
                        throw new IllegalArgumentException("Product not found in cart");
                    }

                    cartRepository.save(cart);
                    entityManager.flush();
                    entityManager.refresh(cart);

                    return cartMapper.toDto(cart);
                });
    }

    @Transactional
    public boolean removeCartItem(UUID cartId, Integer productId) {
        log.info("Removing product {} from cart {}", productId, cartId);

        return cartRepository.findById(cartId)
                .map(cart -> {
                    if (!cart.removeProduct(productId)) {
                        return false;
                    }

                    cartRepository.save(cart);
                    entityManager.flush();
                    entityManager.refresh(cart);

                    return true;
                })
                .orElse(false);
    }

    @Transactional
    public boolean clearCart(UUID cartId) {
        log.info("Clearing all items from cart {}", cartId);

        return cartRepository.findById(cartId)
                .map(cart -> {
                    cart.clearCart();
                    cartRepository.save(cart);
                    entityManager.flush();
                    entityManager.refresh(cart);
                    return true;
                })
                .orElse(false);
    }
}
