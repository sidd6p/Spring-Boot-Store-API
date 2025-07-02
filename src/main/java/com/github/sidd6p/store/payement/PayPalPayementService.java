package com.github.sidd6p.store.payement;

import org.springframework.stereotype.Service;

// @Service annotation marks this class as a Spring service component, making it eligible for
// dependency injection. It's a specialization of @Component that indicates this class contains
// business logic. Spring will automatically detect and register this as a bean in the application context.
// The "paypalPaymentService" value provides a specific name for this bean, useful when multiple implementations exist.
@Service("paypal")
public class PayPalPayementService implements PaymentService {

    @Override
    public void processPayment(Double amount) {
        System.out.println("PayPalPayementService");
        System.out.println("Processing payment with amount: " + amount);
    }
}
