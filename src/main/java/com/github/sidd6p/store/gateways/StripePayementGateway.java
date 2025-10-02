package com.github.sidd6p.store.gateways;

import com.github.sidd6p.store.entities.Order;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class StripePayementGateway implements PayementGateway {
    @Override
    public Session createCheckoutSession(Order order) throws StripeException {
        if (order == null || order.getOrderItems() == null || order.getOrderItems().isEmpty()) {
            throw new IllegalArgumentException("Order must have items to create checkout session");
        }

        var builder = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl("https://localhost:8080/success")
                .setCancelUrl("https://localhost:8080/cancel");

        // Use savedOrder and convert amounts to smallest currency unit (paise for INR)
        order.getOrderItems().forEach(item -> {
            // Fetch product name from the item's product relationship
            String productName = item.getProduct() != null ?
                    item.getProduct().getName() :
                    "Product #" + item.getProductId();

            var lineItem = SessionCreateParams.LineItem.builder()
                    .setQuantity(Long.valueOf(item.getQuantity()))
                    .setPriceData(
                            SessionCreateParams.LineItem.PriceData.builder()
                                    .setCurrency("inr")
                                    // Stripe expects amount in smallest currency unit (paise for INR)
                                    .setUnitAmount(item.getUnitPrice().multiply(BigDecimal.valueOf(100)).longValue())
                                    .setProductData(
                                            SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                    .setName(productName)
                                                    .build()
                                    )
                                    .build()
                    )
                    .build();
            builder.addLineItem(lineItem);
        });

        var session = Session.create(builder.build());
        return session;
    }
}
