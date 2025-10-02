package com.github.sidd6p.store.gateways;


import com.github.sidd6p.store.entities.Order;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;

public interface PayementGateway {
    Session createCheckoutSession(Order order) throws StripeException;
}
