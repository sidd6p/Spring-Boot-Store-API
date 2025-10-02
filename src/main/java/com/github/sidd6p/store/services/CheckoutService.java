package com.github.sidd6p.store.services;

import com.github.sidd6p.store.dtos.CheckoutResponse;
import com.github.sidd6p.store.entities.Cart;
import com.github.sidd6p.store.entities.OrderItems;
import com.github.sidd6p.store.entities.OrderStatus;
import com.github.sidd6p.store.entities.Orders;
import com.github.sidd6p.store.repositories.CartRepository;
import com.github.sidd6p.store.repositories.OrderRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@AllArgsConstructor
public class CheckoutService {
    private final CartRepository cartRepository;
    private final CartService cartService;
    private final AuthService authService;
    private final OrderRepository orderRepository;

    @Transactional
    public CheckoutResponse processCheckout(UUID cartId) {
        // Get the Cart entity directly from repository
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new IllegalArgumentException("Cart not found"));

        if (cart.getCartItems().isEmpty()) {
            throw new IllegalArgumentException("Cart is empty");
        }

        var currentUser = authService.getCurrentUser();
        if (currentUser == null) {
            throw new IllegalStateException("User not authenticated");
        }

        // Calculate total price from cart items
        BigDecimal totalPrice = cart.getCartItems().stream()
                .map(item -> item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        var order = Orders.builder()
                .customerId(currentUser.getId())
                .totalPrice(totalPrice)
                .status(OrderStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .orderItems(new ArrayList<>())
                .build();

        // Save order first to get the ID
        var savedOrder = orderRepository.save(order);

        // Store product names in a map to avoid lazy loading issues
        Map<Integer, String> productNames = new HashMap<>();

        // Now create order items with the saved order ID
        cart.getCartItems().forEach(item -> {
            var product = item.getProduct();
            productNames.put(product.getId(), product.getName());

            var orderItem = OrderItems.builder()
                    .orderId(savedOrder.getId())
                    .productId(product.getId())
                    .unitPrice(product.getPrice())
                    .quantity(item.getQuantity())
                    .totalPrice(product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                    .build();
            savedOrder.getOrderItems().add(orderItem);
        });

        // Save again to persist order items
        orderRepository.save(savedOrder);

        try {
            var builder = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl("https://localhost:8080/success")
                    .setCancelUrl("https://localhost:8080/cancel");

            // Use savedOrder and convert amounts to smallest currency unit (paise for INR)
            savedOrder.getOrderItems().forEach(item -> {
                var lineItem = SessionCreateParams.LineItem.builder()
                        .setQuantity(Long.valueOf(item.getQuantity()))
                        .setPriceData(
                                SessionCreateParams.LineItem.PriceData.builder()
                                        .setCurrency("inr")
                                        // Stripe expects amount in smallest currency unit (paise for INR)
                                        .setUnitAmount(item.getUnitPrice().multiply(BigDecimal.valueOf(100)).longValue())
                                        .setProductData(
                                                SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                        .setName(productNames.get(item.getProductId()))
                                                        .build()
                                        )
                                        .build()
                        )
                        .build();
                builder.addLineItem(lineItem);
            });

            var session = Session.create(builder.build());
            cartService.clearCart(cart.getId());

            return new CheckoutResponse(savedOrder.getId(), session.getUrl());

        } catch (StripeException ex) {
            // If Stripe session creation fails, delete the order to maintain data consistency
            orderRepository.delete(savedOrder);
            throw new RuntimeException("Failed to create payment session: " + ex.getMessage(), ex);
        }
    }
}

