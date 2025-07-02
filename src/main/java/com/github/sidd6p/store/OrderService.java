package com.github.sidd6p.store;

public class OrderService {
    public void placeOrder() {
        var paymentService = new StripePaymentService();
        paymentService.processPayment(100.0);
        System.out.println("Order has been placed successfully.");
    }
}
