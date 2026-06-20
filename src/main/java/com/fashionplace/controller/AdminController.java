package com.fashionplace.controller;

import com.fashionplace.service.AdminService;
import com.fashionplace.service.SupportService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Admin backend. For now any visitor can reach it; real role-based protection
 * arrives in Phase 9 (the dashboard is only linked for the seeded admin).
 */
@Controller
public class AdminController {

    private final AdminService adminService;
    private final SupportService supportService;

    public AdminController(AdminService adminService, SupportService supportService) {
        this.adminService = adminService;
        this.supportService = supportService;
    }

    /**
     * Shows the admin dashboard: counts and tables of users, products and orders.
     *
     * @param model holds the {@code dashboard} snapshot for the view
     * @return the {@code admin/dashboard} view name
     */
    @GetMapping("/admin")
    public String dashboard(Model model) {
        model.addAttribute("dashboard", adminService.loadDashboard());
        return "admin/dashboard";
    }

    /**
     * Soft-deletes a product from the admin dashboard.
     *
     * @param id                 the product to remove
     * @param redirectAttributes flash message shown after redirect
     * @return redirect back to the admin dashboard
     */
    @PostMapping("/admin/products/{id}/delete")
    public String deleteProduct(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        return tryAction(() -> adminService.deleteProduct(id),
                "Could not delete product: ", redirectAttributes, "/admin");
    }

    /**
     * Flags a product so it is hidden from Browse and the cart.
     *
     * @param id                 the product to flag
     * @param redirectAttributes flash message shown after redirect
     * @return redirect back to the admin dashboard
     */
    @PostMapping("/admin/products/{id}/flag")
    public String flagProduct(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        return tryAction(() -> adminService.flagProduct(id),
                "Could not flag product: ", redirectAttributes, "/admin");
    }

    /**
     * Clears an admin flag and restores the product's listing status.
     *
     * @param id                 the product to unflag
     * @param redirectAttributes flash message shown after redirect
     * @return redirect back to the admin dashboard
     */
    @PostMapping("/admin/products/{id}/unflag")
    public String unflagProduct(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        return tryAction(() -> adminService.unflagProduct(id),
                "Could not unflag product: ", redirectAttributes, "/admin");
    }

    /**
     * Disables a user account from the admin dashboard.
     *
     * @param id                 the user to disable
     * @param redirectAttributes flash message shown after redirect
     * @return redirect back to the admin dashboard
     */
    @PostMapping("/admin/users/{id}/disable")
    public String disableUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        return tryAction(() -> adminService.disableUser(id),
                "Could not disable user: ", redirectAttributes, "/admin");
    }

    /**
     * Re-enables a disabled user account from the admin dashboard.
     *
     * @param id                 the user to enable
     * @param redirectAttributes flash message shown after redirect
     * @return redirect back to the admin dashboard
     */
    @PostMapping("/admin/users/{id}/enable")
    public String enableUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        return tryAction(() -> adminService.enableUser(id),
                "Could not enable user: ", redirectAttributes, "/admin");
    }

    /**
     * Shows the support inbox: every conversation across all users.
     *
     * @param model holds {@code threads} for the view
     * @return the {@code admin/support} view name
     */
    @GetMapping("/admin/support")
    public String supportInbox(Model model) {
        model.addAttribute("threads", supportService.allThreads());
        return "admin/support";
    }

    /**
     * Posts a support-team reply to a conversation, then returns to the inbox.
     *
     * @param id                 any message id within the target conversation
     * @param body               the reply text
     * @param redirectAttributes flash message shown after redirect
     * @return redirect back to the support inbox
     */
    @PostMapping("/admin/support/{id}/reply")
    public String replySupport(@PathVariable Long id,
                               @RequestParam("body") String body,
                               RedirectAttributes redirectAttributes) {
        return tryAction(() -> supportService.replyToThread(id, body),
                "Could not send reply: ", redirectAttributes, "/admin/support");
    }

    /**
     * Marks a conversation resolved (CLOSED), then returns to the inbox.
     *
     * @param id                 any message id within the target conversation
     * @param redirectAttributes flash message shown after redirect
     * @return redirect back to the support inbox
     */
    @PostMapping("/admin/support/{id}/close")
    public String closeSupport(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        return tryAction(() -> supportService.closeThread(id),
                "Could not close conversation: ", redirectAttributes, "/admin/support");
    }

    /**
     * Runs {@code action}, and on failure sets an {@code errorMessage} flash attribute.
     *
     * @param action             the admin operation to attempt
     * @param errorPrefix        prefix prepended to the exception message
     * @param redirectAttributes used to carry the error message across the redirect
     * @param redirectPath       the path to redirect to (with or without error)
     * @return a redirect string to {@code redirectPath}
     */
    private String tryAction(Runnable action, String errorPrefix,
                             RedirectAttributes redirectAttributes, String redirectPath) {
        try {
            action.run();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", errorPrefix + e.getMessage());
        }
        return "redirect:" + redirectPath;
    }
}
