package com.fashionplace.restApi;

import com.fashionplace.service.WishlistService;
import com.fashionplace.session.InterestBean;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * JSON API for wishlist actions invoked from the browser (e.g. fetch on Browse / product pages).
 */
@RestController
@RequestMapping("/api/wishlist")
public class WishlistRestController {

    private final WishlistService wishlistService;

    @Resource
    private InterestBean interestBean;

    public WishlistRestController(WishlistService wishlistService) {
        this.wishlistService = wishlistService;
    }

    /**
     * Toggles a product on the current user's wishlist (save if absent, remove if present).
     *
     * @param productId the product to toggle
     * @return {@code {"saved": true}} if now saved, {@code {"saved": false}} if removed
     * @throws org.springframework.security.authentication.AuthenticationCredentialsNotFoundException when the caller is not authenticated
     * @throws java.util.NoSuchElementException when no product exists for {@code productId}
     */
    @PostMapping("/toggle")
    public Map<String, Object> toggle(@RequestParam Long productId) {
        boolean saved = wishlistService.toggleForCurrentUser(productId);
        interestBean.record(productId);
        return Map.of("saved", saved);
    }
}
