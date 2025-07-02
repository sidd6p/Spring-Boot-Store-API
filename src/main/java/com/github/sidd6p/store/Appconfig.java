package com.github.sidd6p.store;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Appconfig.java: Spring {@link Configuration} class for defining beans for notification services and manager.
 * It provides beans for {@link EmailNotificatonService}, {@link SMSNotificationService}, and {@link NotificationManager}.
 * This enables dependency injection of these services throughout the application.
 */

/**
 * The @Configuration annotation marks this class as a source of bean definitions for the Spring context.
 * It tells Spring to process the class and generate Spring beans to be managed by the container.
 */
@Configuration
public class Appconfig {

    /**
     * The @Bean annotation tells Spring that a method instantiates, configures, and initializes a new object to be managed as a bean.
     * Beans are singletons by default and are injected wherever needed, enabling dependency injection (DI).
     */
    @Bean
    public EmailNotificatonService email() {
        return new EmailNotificatonService();
    }

    @Bean
    public SMSNotificationService sms() {
        return new SMSNotificationService();
    }

    @Bean
    public NotificationManager notificationManager() {
        return new NotificationManager(email());
    }
}
