package com.fashionplace.controller;

import com.fashionplace.service.SupportService;
import com.fashionplace.web.SupportForm;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * User-facing customer support: submit a message and view your own conversations.
 */
@Controller
public class SupportController {

    private final SupportService supportService;

    public SupportController(SupportService supportService) {
        this.supportService = supportService;
    }

    /**
     * Shows the support page: a new-message form plus the current user's conversations.
     *
     * @param model holds {@code supportForm} and {@code threads} for the view
     * @return the {@code support} view name
     */
    @GetMapping("/support")
    public String support(Model model) {
        if (!model.containsAttribute("supportForm")) {
            model.addAttribute("supportForm", new SupportForm());
        }
        model.addAttribute("threads", supportService.threadsForCurrentUser());
        return "support";
    }

    /**
     * Submits a new support message for the current user, then redirects back to the page.
     *
     * @param supportForm        the submitted subject and body
     * @param bindingResult      validation errors, if any
     * @param model              repopulated with conversations when validation fails
     * @param redirectAttributes flash message shown after a successful submit
     * @return the support view on error, or a redirect to {@code /support} on success
     */
    @PostMapping("/support")
    public String submit(@Valid @ModelAttribute("supportForm") SupportForm supportForm,
                         BindingResult bindingResult,
                         Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("threads", supportService.threadsForCurrentUser());
            return "support";
        }
        supportService.submitForCurrentUser(supportForm);
        return "redirect:/support";
    }

    /**
     * Adds a reply from the current user to one of their own open conversations.
     *
     * @param id                 any message id within the target conversation
     * @param body               the reply text
     * @param redirectAttributes flash message shown after redirect
     * @return redirect back to the support page
     */
    @PostMapping("/support/{id}/reply")
    public String reply(@PathVariable Long id,
                        @RequestParam("body") String body,
                        RedirectAttributes redirectAttributes) {
        try {
            supportService.replyAsCurrentUser(id, body);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Could not send reply: " + e.getMessage());
        }
        return "redirect:/support";
    }
}
