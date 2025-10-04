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

@SpringBootTest
@AutoConfigureMockMvc  // This enables MockMvc for testing controllers
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
     * This test demonstrates how to:
     * 1. Create a product
     * 2. Save it to the database
     * 3. Retrieve it and verify the data
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
        assertThat(savedProduct.getPrice()).isEqualByComparingTo("100");

        // Cleanup: Delete the test data so it doesn't affect other tests
        productRepository.deleteById(savedProduct.getId());
    }

    /**
     * CONTROLLER TEST: Tests REST API endpoint
     * This test demonstrates how to:
     * 1. Make a GET request to /products
     * 2. Verify the HTTP status is 200 OK
     * 3. Check that response is a JSON array
     */
    @Test
    @WithMockUser  // Mock an authenticated user to bypass Spring Security
    void testGetAllProducts_ReturnsOk() throws Exception {
        // mockMvc.perform() simulates an HTTP request
        mockMvc.perform(get("/products"))  // GET request to /products endpoint
                .andExpect(status().isOk())  // Expect HTTP 200 OK
                .andExpect(jsonPath("$").isArray());  // Expect JSON array response
    }

    /**
     * CONTROLLER TEST: Tests getting a product by ID
     * This shows how to test endpoints with path variables
     */
    @Test
    @WithMockUser  // Mock an authenticated user to bypass Spring Security
    void testGetProductById_WithValidId() throws Exception {
        // ARRANGE: First, create a product to test with
        Category category = Category.builder()
                .name("Books")
                .build();

        Product product = Product.builder()
                .name("Spring Boot Guide")
                .price(new BigDecimal("50"))
                .category(category)
                .build();

        Product savedProduct = productRepository.save(product);

        // ACT & ASSERT: Test the endpoint
        mockMvc.perform(get("/products/" + savedProduct.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Spring Boot Guide"))
                .andExpect(jsonPath("$.price").value(50));

        // Cleanup
        productRepository.deleteById(savedProduct.getId());
    }

    /**
     * CONTROLLER TEST: Tests error handling
     * This shows what happens when requesting a non-existent product
     */
    @Test
    @WithMockUser  // Mock an authenticated user to bypass Spring Security
    void testGetProductById_WithInvalidId_Returns404() throws Exception {
        // ACT & ASSERT: Request a product that doesn't exist
        mockMvc.perform(get("/products/99999"))  // Assuming ID 99999 doesn't exist
                .andExpect(status().isNotFound());  // Expect HTTP 404 Not Found
    }

}
