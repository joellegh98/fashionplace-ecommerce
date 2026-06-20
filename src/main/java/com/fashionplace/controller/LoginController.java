package com.fashionplace.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Serves the login page. Spring Security handles the POST to {@code /login}.
 */
@Controller
public class LoginController {

    /**
     * Shows the sign-in form. Redirects already-authenticated users to the home page.
     *
     * @param error          optional flag set after a failed login attempt
     * @param model          holds {@code loginError} when login failed
     * @param authentication the current security context (null if not logged in)
     * @return redirect to home if already logged in, otherwise the {@code login} view
     */
    @GetMapping("/login")
    public String login(@RequestParam(required = false) String error,
                        Model model,
                        Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            return "redirect:/";
        }
        if (error != null) {
            model.addAttribute("loginError", true);
        }
        return "login";
    }
}
