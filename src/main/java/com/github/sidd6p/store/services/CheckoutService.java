package com.github.sidd6p.store.services;

import com.github.sidd6p.store.dtos.CheckoutResponse;
import com.github.sidd6p.store.entities.Cart;
import com.github.sidd6p.store.entities.Order;
import com.github.sidd6p.store.entities.OrderItems;
import com.github.sidd6p.store.entities.OrderStatus;
import com.github.sidd6p.store.gateways.PayementGateway;
import com.github.sidd6p.store.repositories.CartRepository;
import com.github.sidd6p.store.repositories.OrderRepository;
import com.stripe.exception.StripeException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

@Service
@AllArgsConstructor
public class CheckoutService {
    private final CartRepository cartRepository;
    private final CartService cartService;
    private final AuthService authService;
    private final OrderRepository orderRepository;
    private final PayementGateway payementGateway;

    @Transactional
    public CheckoutResponse processCheckout(UUID cartId) {
        // Get the Cart entity directly from repository
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new IllegalArgumentException("Cart not found"));

        if (cart.getCartItems() == null || cart.getCartItems().isEmpty()) {
            throw new IllegalArgumentException("Cart is empty");
        }

        var currentUser = authService.getCurrentUser();
        if (currentUser == null) {
            throw new IllegalStateException("User not authenticated");
        }

        // Calculate total price from cart items
        BigDecimal totalPrice = cart.getCartItems().stream()
                .map(item -> {
                    if (item.getProduct() == null) {
                        throw new IllegalStateException("Cart item has no associated product");
                    }
                    return item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        var order = Order.builder()
                .customerId(currentUser.getId())
                .totalPrice(totalPrice)
                .status(OrderStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .orderItems(new ArrayList<>())
                .build();

        // Save order first to get the ID
        var savedOrder = orderRepository.save(order);

        // Now create order items with the saved order ID and maintain the product relationship
        cart.getCartItems().forEach(item -> {
            var product = item.getProduct();
            if (product == null) {
                throw new IllegalStateException("Cart item has no associated product");
            }

            var orderItem = OrderItems.builder()
                    .orderId(savedOrder.getId())
                    .productId(product.getId())
                    .unitPrice(product.getPrice())
                    .quantity(item.getQuantity())
                    .totalPrice(product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                    .build();

            // Set the product relationship on the order item so it's available in the gateway
            orderItem.setProduct(product);
            savedOrder.getOrderItems().add(orderItem);
        });

        // Save again to persist order items
        var finalOrder = orderRepository.save(savedOrder);

        try {
            var checkoutSession = payementGateway.createCheckoutSession(finalOrder);

            // Only clear cart after successful payment session creation
            cartService.clearCart(cart.getId());

            return new CheckoutResponse(finalOrder.getId(), checkoutSession.getUrl());

        } catch (StripeException ex) {
            // If Stripe session creation fails, delete the order to maintain data consistency
            orderRepository.delete(finalOrder);
            throw new RuntimeException("Failed to create payment session: " + ex.getMessage(), ex);
        } catch (Exception ex) {
            // Handle any other unexpected errors
            orderRepository.delete(finalOrder);
            throw new RuntimeException("An error occurred during checkout: " + ex.getMessage(), ex);
        }
    }
}

