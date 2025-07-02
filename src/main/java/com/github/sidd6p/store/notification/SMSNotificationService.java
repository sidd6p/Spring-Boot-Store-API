package com.github.sidd6p.store.notification;

import com.github.sidd6p.store.payement.NotificationService;

public class SMSNotificationService implements NotificationService {
    @Override
    public void sendNotification(String message) {
        System.out.println("SMS sent with message: " + message);
    }
}
