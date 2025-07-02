package com.github.sidd6p.store;

import org.springframework.stereotype.Service;

public class SMSNotificationService implements NotificationService {
    @Override
    public void sendNotification(String message) {
        System.out.println("SMS sent with message: " + message);
    }
}
