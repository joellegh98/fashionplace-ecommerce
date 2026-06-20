package com.fashionplace.service;

import com.fashionplace.model.User;
import com.fashionplace.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Loads {@link User} records from the database for Spring Security authentication.
 */
@Service
public class AppUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public AppUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Looks up a user by username and maps it to a Spring Security {@link UserDetails}
     * object (BCrypt hash, role, disabled flag).
     *
     * @param username the login name submitted on sign-in
     * @return the security principal for that user
     * @throws UsernameNotFoundException if no matching account exists
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "No account found for username: " + username));

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPasswordHash())
                .roles(user.getRole())
                .disabled(user.isDisabled())
                .build();
    }
}
