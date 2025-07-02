package com.github.sidd6p.store;

import org.springframework.stereotype.Service;

// @Service annotation marks this class as a Spring service component, making it eligible for
// dependency injection. It's a specialization of @Component that indicates this class contains
// business logic. Spring will automatically detect and register this as a bean in the application context.
// The "paypal" value provides a specific name for this bean, useful when multiple implementations exist.
@Service("paypal")
public class PayPalPayementService implements PaymentService {

    // @Override annotation indicates that this method is overriding a method from the parent interface (PaymentService).
    // It provides compile-time checking to ensure the method signature matches the interface method exactly.
    // This helps catch errors early if the method name, parameters, or return type don't match the interface.
    @Override
    public void processPayment(Double amount) {
        System.out.println("PayPalPayementService");
        System.out.println("Processing payment with amount: " + amount);
    }
}
