package com.github.sidd6p.store.services;

import com.github.sidd6p.store.dtos.CheckoutResponse;
import com.github.sidd6p.store.entities.Cart;
import com.github.sidd6p.store.entities.Order;
import com.github.sidd6p.store.entities.OrderItems;
import com.github.sidd6p.store.entities.OrderStatus;
import com.github.sidd6p.store.gateways.PayementGateway;
import com.github.sidd6p.store.repositories.CartRepository;
import com.github.sidd6p.store.repositories.OrderRepository;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CheckoutService {
    private final CartRepository cartRepository;
    private final CartService cartService;
    private final AuthService authService;
    private final OrderRepository orderRepository;
    private final PayementGateway payementGateway;

    @Value("${stripe.webhookSecret}")
    private String webhookSecret;

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
                .reduce(BigDecimal.ZERO, (a, b) -> a.add(b));

        var order = Order.builder()
                .customerId(currentUser.getId())
                .cartId(cartId)  // Store the cart ID in the order
                .totalPrice(totalPrice)
                .status(OrderStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .orderItems(new ArrayList<>())
                .build();

        // Create order items and establish the relationship
        cart.getCartItems().forEach(item -> {
            var product = item.getProduct();
            if (product == null) {
                throw new IllegalStateException("Cart item has no associated product");
            }

            var orderItem = OrderItems.builder()
                    .productId(product.getId())
                    .unitPrice(product.getPrice())
                    .quantity(item.getQuantity())
                    .totalPrice(product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                    .order(order)  // Set the order relationship instead of orderId
                    .build();

            // Set the product relationship on the order item so it's available in the gateway
            orderItem.setProduct(product);
            order.getOrderItems().add(orderItem);
        });

        // Save the order with all its items
        var savedOrder = orderRepository.save(order);

        try {
            var checkoutSession = payementGateway.createCheckoutSession(savedOrder);

            // Clear the cart immediately after successful checkout session creation
            // The order has been created with its own order items, so the cart can be cleared
            cartService.clearCart(cartId);

            return new CheckoutResponse(savedOrder.getId(), checkoutSession.getUrl());

        } catch (StripeException ex) {
            // If Stripe session creation fails, delete the order to maintain data consistency
            orderRepository.delete(savedOrder);
            throw new RuntimeException("Failed to create payment session: " + ex.getMessage(), ex);
        } catch (Exception ex) {
            // Handle any other unexpected errors
            orderRepository.delete(savedOrder);
            throw new RuntimeException("An error occurred during checkout: " + ex.getMessage(), ex);
        }
    }

    @Transactional
    public void handleWebhookEvent(String signature, String payload) throws SignatureVerificationException {
        var event = Webhook.constructEvent(payload, signature, webhookSecret);

        switch (event.getType()) {
            case "payment_intent.succeeded" -> {
                try {
                    var eventDataObject = event.getData().getObject();
                    if (eventDataObject instanceof PaymentIntent paymentIntent) {
                        String orderId = paymentIntent.getMetadata().get("order_id");
                        if (orderId != null) {
                            orderRepository.findById(Long.valueOf(orderId)).ifPresent(order -> {
                                order.setStatus(OrderStatus.PAID);
                                orderRepository.save(order);
                            });
                            System.out.println("Payment succeeded for order: " + orderId + ", event: " + event.getId());
                        } else {
                            System.out.println("No order_id metadata found in PaymentIntent for event: " + event.getId());
                        }
                    } else {
                        System.out.println("Event data object is not a PaymentIntent for event: " + event.getId());
                        System.out.println("Actual object type: " + (eventDataObject != null ? eventDataObject.getClass().getSimpleName() : "null"));
                        System.out.println("Event data: " + event.getData());
                    }
                } catch (Exception e) {
                    System.out.println("Error processing payment_intent.succeeded: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            case "payment_intent.payment_failed" -> {
                try {
                    var eventDataObject = event.getData().getObject();
                    if (eventDataObject instanceof PaymentIntent paymentIntent) {
                        String orderId = paymentIntent.getMetadata().get("order_id");
                        if (orderId != null) {
                            orderRepository.findById(Long.valueOf(orderId)).ifPresent(order -> {
                                order.setStatus(OrderStatus.FAILED);
                                orderRepository.save(order);
                            });
                            System.out.println("Payment failed for order: " + orderId + ", event: " + event.getId());
                        } else {
                            System.out.println("No order_id metadata found in PaymentIntent for event: " + event.getId());
                        }
                    } else {
                        System.out.println("Event data object is not a PaymentIntent for event: " + event.getId());
                        System.out.println("Actual object type: " + (eventDataObject != null ? eventDataObject.getClass().getSimpleName() : "null"));
                        System.out.println("Event data: " + event.getData());
                    }
                } catch (Exception e) {
                    System.out.println("Error processing payment_intent.payment_failed: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            case "payment_intent.created" -> {
                System.out.println("Payment intent created for event: " + event.getId());
            }
            case "charge.succeeded" -> {
                System.out.println("Charge succeeded for event: " + event.getId());
            }
            case "charge.updated" -> {
                System.out.println("Charge updated for event: " + event.getId());
            }
            default -> System.out.println("Unhandled event type: " + event.getType());
        }
    }
}
