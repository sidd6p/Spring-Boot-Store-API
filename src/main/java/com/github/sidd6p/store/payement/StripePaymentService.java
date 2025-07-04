package com.github.sidd6p.store.payement;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("stripe")
// @Primary annotation marks this bean as the preferred choice when multiple beans of the same type exist.
// When Spring encounters multiple PaymentService implementations and no @Qualifier is specified,
// it will automatically choose this StripePaymentService as the default implementation to inject.
@Primary
public class StripePaymentService implements PaymentService {
    // @Value injects values from config files, env variables, or command-line args (env/args override config files)
    @Value("${stripe.apiUrl}")
    private String apiUrl;

    @Value("${stripe.enabled:false}")
    private boolean enabled;

    @Value("${stripe.timeout:5000}")
    private int timeout;

    // Note: @Value can be used on fields, constructor/method parameters, or setter methods for injection.
    // It CANNOT be used to inject values into local variables inside method bodies.
    @Value("${stripe.supportedCurrencies:USD,EUR}")
    private List<String> supportedCurrencies;

    @Override
    public void processPayment(Double amount) {
        System.out.println("StripePaymentService");
        System.out.println("Processing payment with amount: " + amount);
    }
}
