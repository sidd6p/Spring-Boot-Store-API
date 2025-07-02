package com.github.sidd6p.store;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service("emailNotificationService")
@Primary
public class EmailNotificatonService implements NotificationService {

    // @Override annotation indicates that this method is overriding a method from the parent interface (NotificationService).
    // It provides compile-time checking to ensure the method signature matches the interface method exactly.
    // This helps catch errors early if the method name, parameters, or return type don't match the interface.
    @Override
    public void sendNotification(String message) {
        System.out.println("Email sent with message: " + message);
    }
}
