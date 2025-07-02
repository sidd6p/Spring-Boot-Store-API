package com.github.sidd6p.store.notification;

public class SMSNotificationService implements NotificationService {
    @Override
    public void send(String message, String recipientEmail) {
        System.out.printf("SMS sent with message to %s: %s\n", recipientEmail, message);
    }
}
