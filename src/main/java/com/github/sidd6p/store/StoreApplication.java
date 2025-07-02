package com.github.sidd6p.store;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class StoreApplication {

	public static void main(String[] args) {

		ApplicationContext context = SpringApplication.run(StoreApplication.class, args);
		// Even though we call getBean(OrderService.class) twice, Spring returns the same instance
		// because OrderService is a singleton-scoped bean by default.
		var orderService = context.getBean(OrderService.class);
		orderService.placeOrder();
		var orderService2 = context.getBean(OrderService.class);
		orderService2.placeOrder();

		// Because NotificationManager is defined as a prototype-scoped bean in Appconfig.java,
		// each call to getBean(NotificationManager.class) returns a new instance.
		var notificationManager = context.getBean(NotificationManager.class);
		notificationManager.notify("Order notification sent successfully!");
		var notificationManager2 = context.getBean(NotificationManager.class);
		notificationManager2.notify("Order notification sent successfully again!");
	}

}
