package com.github.sidd6p.store.controllers;


import com.github.sidd6p.store.dtos.CheckoutRequest;
import com.github.sidd6p.store.dtos.CheckoutResponse;
import com.github.sidd6p.store.entities.OrderItems;
import com.github.sidd6p.store.entities.OrderStatus;
import com.github.sidd6p.store.entities.Orders;
import com.github.sidd6p.store.repositories.OrderRepository;
import com.github.sidd6p.store.services.AuthService;
import com.github.sidd6p.store.services.CartService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.ArrayList;

@AllArgsConstructor
@RestController
@RequestMapping("/checkout")
public class CheckoutController {
    private final CartService cartService;
    private final AuthService authService;
    private final OrderRepository orderRepository;

    @PostMapping
    public ResponseEntity<CheckoutResponse> checkout(@Valid @RequestBody CheckoutRequest request) {
        var cart = cartService.getCartById(request.getCartId())
                .orElseThrow(() -> new IllegalArgumentException("Cart not found"));

        if (cart.getCartItems().isEmpty()) {
            throw new IllegalArgumentException("Cart is empty");
        }

        var currentUser = authService.getCurrentUser();
        if (currentUser == null) {
            throw new IllegalStateException("User not authenticated");
        }

        var order = Orders.builder()
                .customerId(currentUser.getId())
                .totalPrice(cart.getPrice())
                .status(OrderStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .orderItems(new ArrayList<>())
                .build();

        // Save order first to get the ID
        var savedOrder = orderRepository.save(order);

        // Now create order items with the saved order ID
        cart.getCartItems().forEach(item -> {
            var orderItem = OrderItems.builder()
                    .orderId(savedOrder.getId())
                    .productId(item.getProduct().getId())
                    .unitPrice(item.getProduct().getPrice())
                    .quantity(item.getQuantity())
                    .totalPrice(item.getTotalPrice())
                    .build();
            savedOrder.getOrderItems().add(orderItem);
        });

        // Save again to persist order items
        orderRepository.save(savedOrder);
        cartService.clearCart(cart.getId());

        return ResponseEntity.ok(new CheckoutResponse(savedOrder.getId()));
    }
}
