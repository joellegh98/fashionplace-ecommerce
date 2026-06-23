package com.fashionplace.service;

import com.fashionplace.model.User;
import com.fashionplace.repository.UserRepository;
import com.fashionplace.dto.RegistrationFormDto;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Creates new {@code USER} accounts from the registration form.
 */
@Service
public class UserRegistrationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserRegistrationService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Checks whether a username is already taken.
     *
     * @param username the username to look up (trimmed by the caller)
     * @return {@code true} if a user with that username exists
     */
    public boolean usernameExists(String username) {
        return userRepository.existsByUsername(username);
    }

    /**
     * Checks whether an email is already registered.
     *
     * @param email the email to look up (trimmed by the caller)
     * @return {@code true} if a user with that email exists
     */
    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * Persists a new regular user with a BCrypt-hashed password.
     *
     * @param form validated registration input
     */
    @Transactional
    public void register(RegistrationFormDto form) {
        User user = new User(
                form.getUsername().trim(),
                form.getEmail().trim(),
                passwordEncoder.encode(form.getPassword()),
                "USER",
                blankToNull(form.getAddress())
        );
        userRepository.save(user);
    }

    private static String blankToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
