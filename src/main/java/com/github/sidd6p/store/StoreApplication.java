package com.github.sidd6p.store;

import com.github.sidd6p.store.entities.Address;
import com.github.sidd6p.store.entities.Profile;
import com.github.sidd6p.store.entities.Tag;
import com.github.sidd6p.store.entities.User;
import com.github.sidd6p.store.notification.NotificationManager;
import com.github.sidd6p.store.order.OrderManager;
import com.github.sidd6p.store.repositories.UserRepository;
import com.github.sidd6p.store.services.ProductServices;
import com.github.sidd6p.store.services.UserServices;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

@SpringBootApplication
public class StoreApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(StoreApplication.class, args);
//		executeStoreOperations(context);
//		executeRepositoryOperations(context);
//		executeUserServices(context);
		executeProductServices(context);
	}


	public static void executeUserServices(ConfigurableApplicationContext context) {
		var userServices = context.getBean(UserServices.class);
		userServices.showEntityStates();
		userServices.showRelatedEntities();
		userServices.fetchAddress();
		userServices.persistRelated();
		userServices.deleteRelated();
	}

	public static void executeProductServices(ConfigurableApplicationContext context) {
		var productServices = context.getBean(ProductServices.class);
		productServices.find("aptop");
	}


	public static void executeRepositoryOperations(ConfigurableApplicationContext context) {
		/*
		   Spring Data JPA uses a powerful mechanism to provide repository implementations at runtime.
		   When you define an interface like UserRepository, you do not need to provide its implementation.
		   At application startup, Spring scans for repository interfaces and automatically creates a proxy class that implements these interfaces.
		   This proxy class is generated dynamically and contains the logic to handle all the repository methods (like save, findAll, etc.) by delegating them to the underlying JPA EntityManager.
		   When you request the UserRepository bean from the Spring context, you receive an instance of this proxy class (the proxy object).
		   Any method call you make (such as save()) is intercepted by the proxy, which then executes the appropriate database operation.
		   This approach allows you to work with repositories as if they were regular objects, while Spring handles all the implementation details behind the scenes.
		*/
		// Get the UserRepository bean from the Spring context. This is a proxy object created by Spring at runtime.
		var userRepository = context.getBean(UserRepository.class);

		// Print the proxy object. This is the actual instance returned by Spring, which implements the UserRepository interface.
		System.out.println("Proxy object: " + userRepository);

		// Print the proxy class. This shows the dynamically generated class that Spring uses to implement the interface.
		System.out.println("Proxy class: " + userRepository.getClass());

		// Print the methods declared in the proxy class. These are the methods you can call on the repository.
		System.out.println("Proxy methods: " + Arrays.toString(userRepository.getClass().getDeclaredMethods()));

		var user1 = User.builder()
				.name("Siddharth Purwar")
				.email("siddpurwar@gmail.com")
				.password("Siddharth")
				.build();
		userRepository.save(user1);
		var id1 = user1.getId();
		System.out.println("Proxy id1: " + id1);
		System.out.println(userRepository.findById(id1).orElse(null));
		userRepository.findAll().forEach(System.out::println);
		userRepository.deleteById(id1);


	}

	public static void executeStoreOperations(ConfigurableApplicationContext context) {
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
			.name("John Doe")
			.email("john.doe@example.com")
			.password("password123")
			.build();
		var address1 = Address.builder()
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
				.bio("This is a sample bio.")
				.phoneNumber("123-456-7890")
				.dateOfBirth(Date.valueOf("1990-01-01"))
				.user(user1)
				.loyaltyPoints(100)
				.build();

		user2.setProfile(profile);
		profile.setUser(user2);
		System.out.println("User2: " + user2);




//		context.close(); // This closes the application context, releasing all resources and beans managed by Spring.
	}

}
