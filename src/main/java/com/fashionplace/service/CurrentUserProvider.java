package com.fashionplace.service;

import com.fashionplace.model.User;
import com.fashionplace.repository.UserRepository;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Resolves the logged-in {@link User} from the Spring Security context.
 */
@Component
public class CurrentUserProvider {

    private final UserRepository userRepository;

    public CurrentUserProvider(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Returns the authenticated user loaded from the database.
     *
     * @return the current user
     * @throws IllegalStateException if nobody is signed in
     */
    public User getCurrentUser() {
        return getCurrentUserOptional()
                .orElseThrow(() -> new IllegalStateException("No authenticated user"));
    }

    /**
     * Returns the authenticated user when a session exists, otherwise empty (e.g. public pages
     * before Phase 9.6 route lockdown).
     *
     * @return the current user, or empty when anonymous
     */
    public Optional<User> getCurrentUserOptional() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            return Optional.empty();
        }
        return userRepository.findByUsername(authentication.getName());
    }
}
