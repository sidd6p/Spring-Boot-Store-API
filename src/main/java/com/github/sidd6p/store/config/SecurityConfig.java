/**
 * This is a Spring Security configuration class. It's responsible for setting up the web security
 * rules for the entire application.
 *
 * How it works:
 * 1. @Configuration: Tells Spring that this class contains configuration settings.
 * 2. @EnableWebSecurity: This is the master switch that enables Spring Security's web security support.
 * 3. @Bean public SecurityFilterChain securityFilterChain(...): This method defines and configures a "filter chain".
 *    A filter chain is a sequence of security checks that every incoming web request must pass through.
 *    Spring Security will automatically pick up this bean and apply it.
 *
 * Key Configurations in this file:
 * - @Order(2): If there are multiple SecurityFilterChain beans (in different configuration files), this number
 *   determines the order of execution. A lower number means higher priority. This chain has a priority of 2,
 *   meaning it will be checked after any chains with a priority of 1. It often acts as a general-purpose
 *   or "fallback" configuration.
 * - sessionManagement(STATELESS): Configures the application to not maintain user sessions between requests.
 *   This is common for REST APIs where each request must contain its own authentication token (e.g., a JWT).
 * - csrf(disable): Disables Cross-Site Request Forgery protection. This is also common for stateless APIs
 *   that are not vulnerable to CSRF attacks in the same way as traditional, session-based web apps.
 * - authorizeHttpRequests: Defines the access rules for different URL endpoints.
 *   - /v2/* and POST /users are marked as permitAll(), meaning they are public and do not require authentication.
 *   - anyRequest().authenticated() is a catch-all rule that ensures any other request to the application
 *     must be authenticated. If an unauthenticated user tries to access these endpoints, they will receive
 *     a 401 Unauthorized error.
 */

/**
 * Note on Auto-Configuration vs. Custom Configuration:
 *
 * When the 'spring-boot-starter-security' dependency is included in the pom.xml, Spring Boot
 * automatically enables security. If no custom configuration like this class is found, Spring
 * applies its default security rules:
 * - All endpoints are protected.
 * - A default login form is generated for authentication (stateful, session-based).
 *
 * By creating this 'SecurityConfig' class and defining our own 'SecurityFilterChain' bean, we are
 * explicitly overriding Spring's default behavior. This custom configuration is designed for a
 * stateless REST API, which is why:
 * 1. We set the session creation policy to STATELESS.
 * 2. We do not configure a form login (`.formLogin()`).
 *
 * As a result, the auto-generated login page disappears. Instead of sessions, the application now
 * expects authentication credentials (like a JWT or Bearer Token) to be sent in the 'Authorization'
 * header of every request to a protected endpoint. If the credentials are missing or invalid, the
 * server responds with a 401 Unauthorized error.
 */
package com.github.sidd6p.store.config;


import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig {
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    @Bean
    @Order(2) // Higher number means lower priority.
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        // stateless session management
       httpSecurity.sessionManagement(c->c.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
               .csrf(AbstractHttpConfigurer::disable)
               .authorizeHttpRequests(c->c
                   .requestMatchers("/v2/*").permitAll()
                   .requestMatchers(HttpMethod.POST, "/users").permitAll()
                   .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
                   .anyRequest().authenticated()
               );
       return httpSecurity.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
       var provider = new DaoAuthenticationProvider();
       provider.setPasswordEncoder(passwordEncoder);
       provider.setUserDetailsService(userDetailsService);
       return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
