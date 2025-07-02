package com.github.sidd6p.store.notification;

public class NotificationManager {
    private final NotificationService notificationService;

    public NotificationManager(NotificationService notificationService) {
        this.notificationService = notificationService;
        System.out.println("NotificationManager created");
    }
    public void notify(String message, String recipientEmail) {
        notificationService.send(message, recipientEmail);
    }
}
