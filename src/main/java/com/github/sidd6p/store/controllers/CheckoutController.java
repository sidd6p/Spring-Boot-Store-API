package com.github.sidd6p.store.controllers;


import com.github.sidd6p.store.dtos.CheckoutRequest;
import com.github.sidd6p.store.dtos.CheckoutResponse;
import com.github.sidd6p.store.services.CheckoutService;
import com.stripe.exception.SignatureVerificationException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/checkout")
public class CheckoutController {
    private final CheckoutService checkoutService;

    @PostMapping
    public ResponseEntity<CheckoutResponse> checkout(@Valid @RequestBody CheckoutRequest request) {
        CheckoutResponse response = checkoutService.processCheckout(request.getCartId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/webhook")
    public ResponseEntity<Void> handleWebhook(@RequestHeader("Stripe-Signature") String signature, @RequestBody String payload) throws SignatureVerificationException {
        checkoutService.handleWebhookEvent(signature, payload);
        return ResponseEntity.ok().build();
    }
}
