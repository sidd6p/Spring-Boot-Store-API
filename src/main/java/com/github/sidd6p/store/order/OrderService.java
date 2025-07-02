package com.github.sidd6p.store.order;

import com.github.sidd6p.store.payement.PaymentService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class OrderService {
    private final PaymentService paymentService;

    // @Qualifier annotation is used to specify which bean to inject when multiple implementations
    // of the same interface exist. Here, "paypalPaymentService" identifies the specific PaymentService implementation
    // to be injected, avoiding ambiguity when Spring tries to autowire the dependency.
    public OrderService(@Qualifier("stripe") PaymentService paymentService) {
        this.paymentService = paymentService;
        System.out.println("Order service created");
    }

    @PostConstruct
    public void init(){
        System.out.println("Order service initialized");
    }

    @PreDestroy
    public void cleanUp() {
        System.out.println("Cleaning up resources in OrderService");
    }

    public void placeOrder() {
        paymentService.processPayment(100.0);
        System.out.println("Order has been placed successfully.");
    }
}
