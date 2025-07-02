package com.github.sidd6p.store.notification;

import org.springframework.beans.factory.annotation.Value;

public class EmailNotificatonService implements NotificationService {

    @Value("${email.smtp.hostUrl}")
    private String host;

    @Value("${email.smtp.port}")
    private String port;


    // @Override annotation indicates that this method is overriding a method from the parent interface (NotificationService).
    // It provides compile-time checking to ensure the method signature matches the interface method exactly.
    // This helps catch errors early if the method name, parameters, or return type don't match the interface.
    @Override
    public void send(String message, String recipientEmail) {
        System.out.printf("Email sent with message to %s: %s\n", recipientEmail, message);
    }
}
