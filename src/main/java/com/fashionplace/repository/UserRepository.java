package com.fashionplace.repository;

import com.fashionplace.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for {@link User} entities. Provides CRUD methods for free.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user by unique username.
     *
     * @param username the login name to look up
     * @return the matching user, or empty if none exists
     */
    Optional<User> findByUsername(String username);

    /**
     * @param username the username to test
     * @return {@code true} if a user with that username exists
     */
    boolean existsByUsername(String username);

    /**
     * @param email the email to test
     * @return {@code true} if a user with that email exists
     */
    boolean existsByEmail(String email);
}
