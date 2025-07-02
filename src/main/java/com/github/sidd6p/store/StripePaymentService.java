package com.github.sidd6p.store;

public class StripePaymentService implements PaymentService {
    @Override
    public void processPayment(Double amount) {
        // Logic to process payment using Stripe
        System.out.println("Processing payment with amount: " + amount);
    }
}
