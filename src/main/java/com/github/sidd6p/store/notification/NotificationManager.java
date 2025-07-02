package com.github.sidd6p.store.notification;

import com.github.sidd6p.store.payement.NotificationService;

public class NotificationManager {
    private final NotificationService notificationService;

    public NotificationManager(NotificationService notificationService) {
        this.notificationService = notificationService;
        System.out.println("NotificationManager created");
    }
    public void notify(String message) {
        notificationService.sendNotification(message);
    }
}
