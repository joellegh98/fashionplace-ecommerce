package com.fashionplace.controller;

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
     * Shows the sign-in form. On failed authentication Spring Security redirects here with
     * {@code ?error}.
     *
     * @param error optional flag set after a failed login attempt
     * @param model holds {@code loginError} when login failed
     * @return the {@code login} view name
     */
    @GetMapping("/login")
    public String login(@RequestParam(required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("loginError", true);
        }
        return "login";
    }
}
