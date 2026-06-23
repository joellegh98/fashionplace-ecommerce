package com.fashionplace.controller;

import com.fashionplace.config.WishlistAuthenticationSuccessHandler;
import com.fashionplace.service.WishlistService;
import com.fashionplace.session.InterestBean;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
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

    private final WishlistService wishlistService;

    @Resource
    private InterestBean interestBean;

    public LoginController(WishlistService wishlistService) {
        this.wishlistService = wishlistService;
    }

    /**
     * Shows the sign-in form. Redirects already-authenticated users to the home page.
     *
     * @param error              optional flag set after a failed login attempt
     * @param wishlistProductId  optional product the guest tried to save before logging in
     * @param model              holds {@code loginError} when login failed
     * @param authentication     the current security context (null if not logged in)
     * @return redirect to home if already logged in, otherwise the {@code login} view
     */
    @GetMapping("/login")
    public String login(@RequestParam(required = false) String error,
                        @RequestParam(required = false) Long wishlistProductId,
                        HttpSession session,
                        Model model,
                        Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            if (wishlistProductId != null) {
                wishlistService.toggleForCurrentUser(wishlistProductId);
                interestBean.record(wishlistProductId);
                return "redirect:/wishlist";
            }
            return "redirect:/";
        }
        if (wishlistProductId != null) {
            session.setAttribute(
                    WishlistAuthenticationSuccessHandler.PENDING_WISHLIST_PRODUCT_ID,
                    wishlistProductId);
        }
        if (error != null) {
            model.addAttribute("loginError", true);
        }
        return "login";
    }
}
