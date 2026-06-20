package com.fashionplace.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security configuration: form login, logout, CSRF protection, and role-based access.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Hashes and verifies passwords (used by the data seeder and authentication).
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // Public assets and error handling
                        .requestMatchers("/css/**", "/js/**", "/favicon.jpeg", "/error").permitAll()
                        // Auth pages
                        .requestMatchers("/login", "/logout", "/register").permitAll()
                        // Admin area (includes /admin/support)
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        // Authenticated user areas
                        .requestMatchers("/sell").authenticated()
                        .requestMatchers("/orders", "/my-products").authenticated()
                        .requestMatchers("/wishlist/**").authenticated()
                        .requestMatchers("/support/**").authenticated()
                        .requestMatchers("/checkout/**").authenticated()
                        .requestMatchers("/product/*/edit", "/product/*/delete", "/product/*/review")
                                .authenticated()
                        // Public browsing, cart, and product detail
                        .requestMatchers("/", "/browse", "/cart/**", "/api/**").permitAll()
                        .requestMatchers("/product/*/image", "/product/*").permitAll()
                        .anyRequest().permitAll()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        // false = after login, return to the page the user originally requested (e.g. /checkout)
                        .defaultSuccessUrl("/", false)
                        .failureUrl("/login?error")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/")
                        .permitAll()
                );
        // CSRF protection is enabled by default (no csrf.disable()).
        return http.build();
    }
}
