package com.github.sidd6p.store;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service("stripe")
// @Primary annotation marks this bean as the preferred choice when multiple beans of the same type exist.
// When Spring encounters multiple PaymentService implementations and no @Qualifier is specified,
// it will automatically choose this StripePaymentService as the default implementation to inject.
@Primary
public class StripePaymentService implements PaymentService {
    @Override
    public void processPayment(Double amount) {
        System.out.println("StripePaymentService");
        System.out.println("Processing payment with amount: " + amount);
    }
}
