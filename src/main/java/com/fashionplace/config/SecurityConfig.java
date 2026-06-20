package com.fashionplace.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security configuration: form login, logout, and CSRF protection.
 * Route/role rules are added in Phase 9.6.
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
                        // Login/logout endpoints (custom pages in Phase 9.4)
                        .requestMatchers("/login", "/logout").permitAll()
                        // Full route/role lockdown in Phase 9.6
                        .anyRequest().permitAll()
                )
                .formLogin(form -> form
                        .defaultSuccessUrl("/", true)
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
