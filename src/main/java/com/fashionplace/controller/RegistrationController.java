package com.fashionplace.controller;

import com.fashionplace.service.UserRegistrationService;
import com.fashionplace.web.RegistrationForm;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Registration page: creates a new {@code USER} account after server-side validation.
 */
@Controller
public class RegistrationController {

    private final UserRegistrationService userRegistrationService;

    public RegistrationController(UserRegistrationService userRegistrationService) {
        this.userRegistrationService = userRegistrationService;
    }

    /**
     * Shows the empty registration form. Redirects already-authenticated users to the home page.
     *
     * @param model          holds the {@code registrationForm}
     * @param authentication the current security context (null if not logged in)
     * @return redirect to home if already logged in, otherwise the {@code register} view
     */
    @GetMapping("/register")
    public String registerForm(Model model, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            return "redirect:/";
        }
        model.addAttribute("registrationForm", new RegistrationForm());
        return "register";
    }

    /**
     * Validates the submission, creates the account, and redirects to the login page.
     *
     * @param form              submitted registration details
     * @param bindingResult     validation and business-rule errors
     * @param redirectAttributes carries a success flash message on redirect
     * @return redirect to login on success, or the form again on error
     */
    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("registrationForm") RegistrationForm form,
                           BindingResult bindingResult,
                           RedirectAttributes redirectAttributes) {
        validateBusinessRules(form, bindingResult);
        if (bindingResult.hasErrors()) {
            return "register";
        }

        userRegistrationService.register(form);
        redirectAttributes.addFlashAttribute(
                "successMessage", "Account created. Please log in with your new credentials.");
        return "redirect:/login";
    }

    private void validateBusinessRules(RegistrationForm form, BindingResult bindingResult) {
        if (form.getPassword() != null && form.getConfirmPassword() != null
                && !form.getPassword().equals(form.getConfirmPassword())) {
            bindingResult.rejectValue("confirmPassword", "mismatch", "Passwords do not match.");
        }
        if (form.getUsername() != null && userRegistrationService.usernameExists(form.getUsername().trim())) {
            bindingResult.rejectValue("username", "taken", "That username is already taken.");
        }
        if (form.getEmail() != null && userRegistrationService.emailExists(form.getEmail().trim())) {
            bindingResult.rejectValue("email", "taken", "That email is already registered.");
        }
    }
}
