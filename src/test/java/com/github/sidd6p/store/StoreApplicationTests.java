package com.github.sidd6p.store;

import com.github.sidd6p.store.entities.Category;
import com.github.sidd6p.store.entities.Product;
import com.github.sidd6p.store.repositories.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

/**

 * @SpringBootTest: Loads the full application context (all beans, configs, security)
 * @AutoConfigureMockMvc: Enables MockMvc for testing REST endpoints without starting a real HTTP server
 * 
 * IMPORTANT: Since this app uses Spring Security, any HTTP request tests need authentication.
 * Solution: Use @WithMockUser on test methods to simulate an authenticated user.
 */
@SpringBootTest
@AutoConfigureMockMvc  
class StoreApplicationTests {

    // @Autowired: Spring injects the bean automatically (Dependency Injection)
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MockMvc mockMvc;  // MockMvc helps test REST endpoints without starting a real server

    /**
     * SMOKE TEST: Verifies the application context loads successfully
     * If this fails, there's a configuration problem in your app
     */
    @Test
    void contextLoads() {
        // Empty test - just checking if Spring Boot starts without errors
    }

    /**
     * REPOSITORY TEST: Tests database operations
     * 
     * NOTE: Repository tests don't need @WithMockUser because they interact directly
     * with the database, bypassing the web layer and Spring Security.
     */
    @Test
    void testProductRepository_SaveAndFind() {
        // ARRANGE: Set up test data
        Category category = Category.builder()
                .name("Electronics")
                .build();

        Product product = Product.builder()
                .name("Test Laptop")
                .price(new BigDecimal("100"))  
                .category(category)
                .build();

        // ACT: Perform the action you want to test
        Product savedProduct = productRepository.save(product);

        // ASSERT: Verify the results
        assertThat(savedProduct.getId()).isNotNull();  // ID should be auto-generated
        assertThat(savedProduct.getName()).isEqualTo("Test Laptop");
        // Use isEqualByComparingTo for BigDecimal - compares value ignoring scale (100 vs 100.00)
        assertThat(savedProduct.getPrice()).isEqualByComparingTo("100");

        // Cleanup: Delete the test data so it doesn't affect other tests
        productRepository.deleteById(savedProduct.getId());
    }

    /**
     * CONTROLLER TEST: Tests REST API endpoint
     * @WithMockUser: REQUIRED for controller tests!
     * - Your app has Spring Security enabled
     * - Without this annotation, the test would get Unauthorized
     * - This simulates a logged-in user making the request
     * - You can also specify roles: @WithMockUser(roles = "ADMIN")
     */
    @Test
    @WithMockUser  // Mock an authenticated user to bypass Spring Security
    void testGetAllProducts_ReturnsOk() throws Exception {
        // mockMvc.perform() simulates an HTTP request
        mockMvc.perform(get("/products"))  // GET request to /products endpoint
                .andExpect(status().isOk())  // Expect HTTP 200 OK
                .andExpect(jsonPath("$").isArray());  // Expect JSON array response
    }

}
