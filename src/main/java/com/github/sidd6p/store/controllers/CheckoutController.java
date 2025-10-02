package com.github.sidd6p.store.controllers;


import com.github.sidd6p.store.dtos.CheckoutRequest;
import com.github.sidd6p.store.dtos.CheckoutResponse;
import com.github.sidd6p.store.services.CheckoutService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.net.Webhook;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/checkout")
public class CheckoutController {
    private final CheckoutService checkoutService;
    @Value("${stripe.webhookSecret}")
    private String webhookSecret;

    @PostMapping
    public ResponseEntity<CheckoutResponse> checkout(@Valid @RequestBody CheckoutRequest request) {
        CheckoutResponse response = checkoutService.processCheckout(request.getCartId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/webhook")
    public ResponseEntity<Void> handlewebhook(@RequestHeader("Stripe-Signature") String signature, @RequestBody String payload) throws SignatureVerificationException {
        var event = Webhook.constructEvent(payload, signature, webhookSecret);

        switch (event.getType()) {
            case "payment_intent.succeeded" -> {
                System.out.println("Payment succeeded for event: " + event.getId());
                // TODO: Add your success handling logic here
            }
            case "payment_intent.payment_failed" -> {
                System.out.println("Payment failed for event: " + event.getId());
                // TODO: Add your failure handling logic here
            }
            default -> System.out.println("Unhandled event type: " + event.getType());
        }

        return ResponseEntity.ok().build();
    }
}
