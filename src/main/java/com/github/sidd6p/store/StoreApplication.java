package com.github.sidd6p.store;

import com.github.sidd6p.store.notification.NotificationManager;
import com.github.sidd6p.store.order.OrderManager;
import com.github.sidd6p.store.user.User;
import com.github.sidd6p.store.user.UserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class StoreApplication {

	public static void main(String[] args) {

		// ConfigurableApplicationContext gives us more options to interact with the application context, such as closing the context or registering shutdown hooks. It extends ApplicationContext, providing additional configuration and lifecycle management methods.
		ConfigurableApplicationContext context = SpringApplication.run(StoreApplication.class, args);

		// Even though we call getBean(OrderService.class) twice, Spring returns the same instance
		// because OrderService is a singleton-scoped bean by default.
		var orderService = context.getBean(OrderManager.class);
		var orderService2 = context.getBean(OrderManager.class);
//		orderService.placeOrder();
//		orderService2.placeOrder();

		// Because NotificationManager is defined as a prototype-scoped bean in Appconfig.java,
		// each call to getBean(NotificationManager.class) returns a new instance.
		var notificationManager = context.getBean(NotificationManager.class);
		var notificationManager2 = context.getBean(NotificationManager.class);
//		notificationManager.notify("Order placed successfully!", "siddpurwar@gmail.com");
//		notificationManager2.notify("Order placed successfully!", "siddpurwar@gmail.com");

		var userService = context.getBean(UserService.class);
		User newUser = new User(123131, "Siddhartha", "siddpurwar@gmail.com", "fghjj@feds");
		userService.registerUser(newUser);


//		context.close(); // This closes the application context, releasing all resources and beans managed by Spring.
	}

}
