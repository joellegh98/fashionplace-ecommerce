package com.fashionplace.service;

import com.fashionplace.model.Product;
import com.fashionplace.model.User;
import com.fashionplace.model.WishlistItem;
import com.fashionplace.repository.WishlistItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Business logic for the per-user wishlist.
 */
@Service
public class WishlistService {

    private final WishlistItemRepository wishlistItemRepository;
    private final ProductService productService;
    private final CurrentUserProvider currentUserProvider;

    public WishlistService(WishlistItemRepository wishlistItemRepository,
                           ProductService productService,
                           CurrentUserProvider currentUserProvider) {
        this.wishlistItemRepository = wishlistItemRepository;
        this.productService = productService;
        this.currentUserProvider = currentUserProvider;
    }

    /**
     * Returns the current user's saved items.
     *
     * @return the current user's wishlist items
     */
    public List<WishlistItem> findForCurrentUser() {
        return currentUserProvider.getCurrentUserOptional()
                .map(wishlistItemRepository::findByOwner)
                .orElse(List.of());
    }

    /**
     * Toggles a product on the current user's wishlist: saves it if absent, removes it if
     * already saved. Works for sold products too.
     *
     * @param productId the product to toggle
     * @return {@code true} if the product is now saved, {@code false} if it was removed
     */
    @Transactional
    public boolean toggleForCurrentUser(Long productId) {
        User owner = currentUserProvider.getCurrentUser();
        Product product = productService.findById(productId);
        if (wishlistItemRepository.existsByOwnerAndProduct(owner, product)) {
            wishlistItemRepository.deleteByOwnerAndProduct(owner, product);
            return false;
        }
        wishlistItemRepository.save(new WishlistItem(owner, product));
        return true;
    }

    /**
     * Removes a wishlist item if it belongs to the current user.
     *
     * @param itemId the wishlist item id to remove
     */
    public void removeForCurrentUser(Long itemId) {
        User owner = currentUserProvider.getCurrentUser();
        wishlistItemRepository.findById(itemId)
                .filter(item -> item.getOwner().getId().equals(owner.getId()))
                .ifPresent(wishlistItemRepository::delete);
    }

    /**
     * Tells whether the current user has already saved the given product.
     *
     * @param product the product to test
     * @return {@code true} if it is on the current user's wishlist
     */
    public boolean isOnCurrentUserWishlist(Product product) {
        return currentUserProvider.getCurrentUserOptional()
                .map(user -> wishlistItemRepository.existsByOwnerAndProduct(user, product))
                .orElse(false);
    }

    /**
     * Returns the ids of products the current user has saved, for highlighting list views.
     *
     * @return saved product ids for the current user
     */
    public Set<Long> savedProductIdsForCurrentUser() {
        return findForCurrentUser().stream()
                .map(item -> item.getProduct().getId())
                .collect(Collectors.toSet());
    }
}
