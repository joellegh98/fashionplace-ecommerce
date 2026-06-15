package com.fashionplace.service;

import com.fashionplace.model.User;
import com.fashionplace.repository.UserRepository;
import org.springframework.stereotype.Component;

/**
 * Stand-in for the logged-in user until Phase 9 adds real Spring Security.
 *
 * <p>Always returns the seeded user {@code alice}. Phase 9 replaces this with
 * the authenticated principal from the security context.</p>
 */
@Component
public class CurrentUserProvider {

    /** Seeded regular user used as the fake "logged-in" account. */
    private static final String DEFAULT_USERNAME = "alice";

    private final UserRepository userRepository;

    public CurrentUserProvider(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Returns the current user (always {@code alice} for now).
     *
     * @return the seeded user
     * @throws IllegalStateException if the seeded user is missing from the database
     */
    public User getCurrentUser() {
        return userRepository.findByUsername(DEFAULT_USERNAME)
                .orElseThrow(() -> new IllegalStateException(
                        "Seeded user '" + DEFAULT_USERNAME + "' not found — check DataSeeder"));
    }
}
