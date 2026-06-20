package com.fashionplace.config;

import com.fashionplace.model.User;
import com.fashionplace.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Ensures a default {@code ADMIN} account exists on every startup (creates it only when missing).
 * Works on an empty database and recreates the admin if the account was deleted.
 */
@Component
@Order(2)
public class AdminInitializer implements CommandLineRunner {

    /** Default admin login name. */
    public static final String ADMIN_USERNAME = "admin";

    /** Default admin email. */
    public static final String ADMIN_EMAIL = "admin@fashionplace.com";

    /** Default dev password (BCrypt-hashed before persist). */
    public static final String ADMIN_PASSWORD = "password";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userRepository.existsByUsername(ADMIN_USERNAME)) {
            return;
        }
        userRepository.save(new User(
                ADMIN_USERNAME,
                ADMIN_EMAIL,
                passwordEncoder.encode(ADMIN_PASSWORD),
                "ADMIN",
                "1 Market Street, Springfield"
        ));
    }
}
