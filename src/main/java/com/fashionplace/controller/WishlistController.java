package com.fashionplace.controller;

import com.fashionplace.service.WishlistService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Serves the wishlist page and form-based remove actions.
 */
@Controller
public class WishlistController {

    private final WishlistService wishlistService;

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
