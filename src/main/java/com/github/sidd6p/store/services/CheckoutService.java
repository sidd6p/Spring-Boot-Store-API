package com.github.sidd6p.store.services;

import com.github.sidd6p.store.dtos.CheckoutResponse;
import com.github.sidd6p.store.entities.OrderItems;
import com.github.sidd6p.store.entities.OrderStatus;
import com.github.sidd6p.store.entities.Orders;
import com.github.sidd6p.store.repositories.OrderRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

@Service
@AllArgsConstructor
public class CheckoutService {
    private final CartService cartService;
    private final AuthService authService;
    private final OrderRepository orderRepository;

    @Transactional
    public CheckoutResponse processCheckout(UUID cartId) {
        var cart = cartService.getCartById(cartId)
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

        return new CheckoutResponse(savedOrder.getId());
    }
}

