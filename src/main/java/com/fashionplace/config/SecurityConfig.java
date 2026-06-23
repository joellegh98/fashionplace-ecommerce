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
     *
     * @return a BCrypt {@link PasswordEncoder} bean
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configures HTTP security: URL authorization, form login, logout, and CSRF.
     *
     * @param http                          the {@link HttpSecurity} to customize
     * @param wishlistLoginSuccessHandler   handles post-login wishlist pending actions
     * @return the configured security filter chain
     * @throws Exception when the security configuration cannot be applied
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   WishlistAuthenticationSuccessHandler wishlistLoginSuccessHandler)
            throws Exception {
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
                        .requestMatchers("/wishlist", "/wishlist/remove/**").authenticated()
                        .requestMatchers("/api/wishlist/**").authenticated()
                        .requestMatchers("/support/**").authenticated()
                        .requestMatchers("/checkout/**").authenticated()
                        .requestMatchers("/product/*/edit", "/product/*/delete", "/product/*/review")
                                .authenticated()
                        // Public browsing, cart, and product detail
                        .requestMatchers("/", "/browse", "/cart/**", "/api/**").permitAll()
                        .requestMatchers("/product/*").permitAll()
                        .anyRequest().permitAll()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .successHandler(wishlistLoginSuccessHandler)
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
