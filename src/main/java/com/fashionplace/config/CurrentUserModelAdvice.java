package com.fashionplace.config;

import com.fashionplace.service.CurrentUserProvider;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Exposes current-user info needed by the shared layout (e.g. whether to show the
 * Admin nav link).
 */
@ControllerAdvice
public class CurrentUserModelAdvice {

    private final CurrentUserProvider currentUserProvider;

    public CurrentUserModelAdvice(CurrentUserProvider currentUserProvider) {
        this.currentUserProvider = currentUserProvider;
    }

    /**
     * @return {@code true} if the signed-in user has the {@code ADMIN} role
     */
    @ModelAttribute("isAdmin")
    public boolean isAdmin() {
        return currentUserProvider.getCurrentUserOptional()
                .map(user -> "ADMIN".equals(user.getRole()))
                .orElse(false);
    }
}
