package com.fashionplace.controller;

import com.fashionplace.service.WishlistService;
import com.fashionplace.session.InterestBean;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import java.util.Map;

/**
 * Handles the wishlist page and its add/remove actions (attributed to the fake current user).
 */
@Controller
public class WishlistController {

    private final WishlistService wishlistService;

    @Resource
    private InterestBean interestBean;

    public WishlistController(WishlistService wishlistService) {
        this.wishlistService = wishlistService;
    }

    /**
     * Lists the current user's saved products.
     *
     * @param model holds {@code wishlistItems} for the view
     * @return the {@code wishlist} view name
     */
    @GetMapping("/wishlist")
    public String wishlist(Model model) {
        model.addAttribute("wishlistItems", wishlistService.findForCurrentUser());
        return "wishlist";
    }

    /**
     * Toggles a product on the current user's wishlist (save if absent, remove if present).
     * Returns JSON so the page can update the button in place (via fetch) without a full
     * reload. Works for sold products too.
     *
     * @param productId the product to toggle
     * @return {@code {"saved": true}} if now saved, {@code {"saved": false}} if removed
     */
    @PostMapping("/wishlist/toggle")
    @ResponseBody
    public Map<String, Object> toggle(@RequestParam Long productId) {
        boolean saved = wishlistService.toggleForCurrentUser(productId);
        interestBean.record(productId);
        return Map.of("saved", saved);
    }

    /**
     * Removes an item from the current user's wishlist.
     *
     * @param id        the wishlist item id
     * @param returnUrl optional page to return to (defaults to the wishlist)
     * @return redirect to the originating page
     */
    @PostMapping("/wishlist/remove/{id}")
    public String remove(@PathVariable Long id,
                         @RequestParam(required = false) String returnUrl) {
        wishlistService.removeForCurrentUser(id);
        return "redirect:" + safeReturnUrl(returnUrl);
    }

    /** Restricts redirects to local paths to avoid open-redirect issues. */
    private String safeReturnUrl(String returnUrl) {
        if (returnUrl != null && returnUrl.startsWith("/")) {
            return returnUrl;
        }
        return "/wishlist";
    }
}
