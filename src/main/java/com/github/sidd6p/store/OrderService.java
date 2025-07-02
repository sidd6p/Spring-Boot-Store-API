package com.github.sidd6p.store;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
public class OrderService {
    private final PaymentService paymentService;

    // @Qualifier annotation is used to specify which bean to inject when multiple implementations
    // of the same interface exist. Here, "paypalPaymentService" identifies the specific PaymentService implementation
    // to be injected, avoiding ambiguity when Spring tries to autowire the dependency.
    public OrderService(@Qualifier("paypalPaymentService") PaymentService paymentService) {
        this.paymentService = paymentService;
    }
    public void placeOrder() {
        paymentService.processPayment(100.0);
        System.out.println("Order has been placed successfully.");
    }
}
