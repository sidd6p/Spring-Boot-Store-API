package com.github.sidd6p.store;

import org.springframework.stereotype.Service;

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
