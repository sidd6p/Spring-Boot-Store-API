package com.github.sidd6p.store;

import org.springframework.stereotype.Service;

@Service
public class NotificationManager {
    private final NotificationService notificationService;

    public NotificationManager(NotificationService notificationService) {
        this.notificationService = notificationService;
    }
    public void notify(String message) {
        notificationService.sendNotification(message);
    }
}
