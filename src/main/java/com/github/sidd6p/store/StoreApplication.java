package com.github.sidd6p.store;

import com.github.sidd6p.store.entities.*;
import com.github.sidd6p.store.notification.NotificationManager;
import com.github.sidd6p.store.order.OrderManager;
import com.github.sidd6p.store.repositories.UserRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

/*
 * KEY CONCEPTS:
 * 2. WHY repository DON'T MIX DATA:
 *    Repositories are STATELESS - they don't store data, only provide methods
 *
 *    Example:
 *    Thread-A: repository.save(user1) -> sends to DB immediately
 *    Thread-B: repository.save(user2) -> sends to DB immediately
 *    Thread-C: repository.findById(5) -> queries DB, returns result
 *
 *    No data mixing because:
 *    - Repository doesn't hold user1, user2, or any User objects
 *    - Data lives in: database (permanent) and local variables (temporary)
 *    - Repository = bank teller (shared), Data = money (stored in vault, not in teller)
 *
 *    STATELESS (safe for singleton): Service, Repository, Controller - only logic, no data storage
 *    STATEFUL (needs prototype): Shopping cart, user session - stores changing data
 */

// @SpringBootApplication: Combines @Configuration, @EnableAutoConfiguration, and @ComponentScan
// Enables auto-configuration, component scanning, and Java-based configuration in one annotation
@SpringBootApplication
public class StoreApplication {

    public static void main(String[] args) {
        // SpringApplication.run() bootstraps the application, creates ApplicationContext, and manages beans
        // ConfigurableApplicationContext provides access to the Spring container and its managed beans
        ConfigurableApplicationContext context = SpringApplication.run(StoreApplication.class, args);
        executeStoreOperations(context);
        executeRepositoryOperations(context);
    }





    /*
     * SPRING DATA JPA: Repository Pattern & Dynamic Proxies
     *
     * How it works:
     * 1. Define repository interface (e.g., UserRepository extends JpaRepository) - no implementation needed
     * 2. At startup, Spring scans for repository interfaces and creates proxy classes dynamically
     * 3. Proxy class contains logic for all repository methods (save, findAll, findById, etc.)
     * 4. All operations delegate to the underlying JPA EntityManager
     * 5. When you call context.getBean(UserRepository.class), you get the proxy instance
     * 6. Method calls (e.g., save()) are intercepted by the proxy, which executes the database operation
     *
     */
    public static void executeRepositoryOperations(ConfigurableApplicationContext context) {
        // context.getBean() retrieves a bean (managed object) from the Spring IoC container
        // The bean IS a proxy object - in Spring, "bean" means any container-managed object
        // So: proxy object = bean, they're the same thing, just different terminology
        var userRepository = context.getBean(UserRepository.class);

        // Proxy object: Shows the actual instance Spring created (not your interface)
        System.out.println("Proxy object: " + userRepository);

        // Proxy class: Reveals the dynamically generated class (e.g., SimpleJpaRepository$Proxy)
        System.out.println("Proxy class: " + userRepository.getClass());

        // Proxy methods: All methods available on the proxy, including inherited ones
        System.out.println("Proxy methods: " + Arrays.toString(userRepository.getClass().getDeclaredMethods()));

        var user1 = User.builder()
                .name("Siddharth Purwar")
                .email("siddpurwar@gmail.com")
                .password("Siddharth")
                .role(Role.USER)
                .build();
        userRepository.save(user1);
        var id1 = user1.getId();
        System.out.println("Proxy id1: " + id1);
        System.out.println(userRepository.findById(id1).orElse(null));
        userRepository.findAll().forEach(System.out::println);
        userRepository.deleteById(id1);


    }

    // BEAN SCOPES: Singleton vs Prototype
    public static void executeStoreOperations(ConfigurableApplicationContext context) {
        // SINGLETON SCOPE (default): Multiple getBean() calls return the same instance
        // Single shared instance per Spring IoC container - stateless and efficient
        var orderService = context.getBean(OrderManager.class);
        var orderService2 = context.getBean(OrderManager.class);
//		orderService.placeOrder();
//		orderService2.placeOrder();

        // PROTOTYPE SCOPE: Each getBean() call creates a new instance
        // Use for stateful beans where each client needs independent state
        var notificationManager = context.getBean(NotificationManager.class);
        var notificationManager2 = context.getBean(NotificationManager.class);
//		notificationManager.notify("Order placed successfully!", "siddpurwar@gmail.com");
//		notificationManager2.notify("Order placed successfully!", "siddpurwar@gmail.com");

        // OBJECT CONSTRUCTION: Three approaches
        // 1. Default constructor + setters (verbose but explicit)
        var user1 = new User();

        // 2. All-args constructor (error-prone with many parameters)
        var user2 = new User(null, null, "siddpurwar6@gmail.com", null, null, new ArrayList<>(), new HashSet<>(), null);

        // 3. LOMBOK BUILDER PATTERN: 
        // @Builder annotation generates a builder() static method and inner Builder class at compile-time
        // No 'new' keyword needed - User.builder() returns the Builder instance
        var user3 = User.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .password("password123")
                .build();
        


//	context.close(); // Shuts down ApplicationContext, destroys singleton beans, and releases resources
    }

}
