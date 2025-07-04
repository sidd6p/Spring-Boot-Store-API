package com.github.sidd6p.store;

import com.github.sidd6p.store.models.Address;
import com.github.sidd6p.store.models.Profile;
import com.github.sidd6p.store.models.Tag;
import com.github.sidd6p.store.models.User;
import com.github.sidd6p.store.notification.NotificationManager;
import com.github.sidd6p.store.order.OrderManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.ArrayList;
import java.util.HashSet;

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

		var user1 = new User();
		user1.setEmail("siddpurwar@gmail.com");
		System.out.println("User1: " + user1);

		var user2 = new User(null, null, "siddpurwar6@gmail.com", null, new ArrayList<>(), new HashSet<>(), null);
		System.out.println("User2: " + user2);

		// We are not using the 'new' keyword here because Lombok's @Builder generates a builder() method.
		// The builder pattern provides a flexible and readable way to construct objects, especially with many fields.
		// Instead of calling a constructor directly, we use User.builder()...build() to set properties fluently and create the object.
		var user3 = User.builder()
			.id(3L)
			.name("John Doe")
			.email("john.doe@example.com")
			.password("password123")
			.build();
		var address1 = Address.builder()
				.id(1L)
				.zip("12345")
				.city("New York")
				.street("123 Main St")
				.user(user3)
				.build();
		user3.addAddress(address1);
		System.out.println("User3: " + user3);
		user3.removeAddress(address1);
		System.out.println("User3: " + user3);


		var tag1 = new Tag("tag1");
		user1.addTag(tag1);
		System.out.println("User1: " + user1);
		user1.removeTag(tag1);
		System.out.println("User1: " + user1);


		var profile = Profile.builder()
				.id(1L)
				.bio("This is a sample bio.")
				.phoneNumber("123-456-7890")
				.dateOfBirth(java.sql.Date.valueOf("1990-01-01"))
				.user(user1)
				.loyaltyPoints(100)
				.build();

		user2.setProfile(profile);
		profile.setUser(user2);
		System.out.println("User2: " + user2);


//		context.close(); // This closes the application context, releasing all resources and beans managed by Spring.
	}

}
