package com.fashionplace.service;

import com.fashionplace.model.Order;
import com.fashionplace.model.OrderItem;
import com.fashionplace.model.Product;
import com.fashionplace.model.User;
import com.fashionplace.model.WishlistItem;
import com.fashionplace.repository.ProductRepository;
import com.fashionplace.session.CartBean;
import com.fashionplace.session.InterestBean;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

/**
 * Suggests products for the current user based on what they have shown interest in.
 *
 * <p>Pure service/query logic — no new entity. It remembers every product the user added to
 * their cart or wishlist (see {@link InterestBean}, kept even after removal), plus the
 * categories of their past orders, and recommends those items themselves alongside other
 * {@code ACTIVE} products in the same categories, newest first.</p>
 */
@Service
public class RecommendationService {

    /** Maximum number of recommendations to surface at once. */
    private static final int MAX_RECOMMENDATIONS = 5;

    private final OrderService orderService;
    private final ProductRepository productRepository;
    private final CurrentUserProvider currentUserProvider;
    private final WishlistService wishlistService;

    @Resource
    private CartBean cartBean;

    @Resource
    private InterestBean interestBean;

    public RecommendationService(OrderService orderService,
                                 ProductRepository productRepository,
                                 CurrentUserProvider currentUserProvider,
                                 WishlistService wishlistService) {
        this.orderService = orderService;
        this.productRepository = productRepository;
        this.currentUserProvider = currentUserProvider;
        this.wishlistService = wishlistService;
    }

    /**
     * Returns up to five recommendations for the current user, newest first.
     *
     * <p>Items the user added to their cart or wishlist are recommended directly — even after
     * they were removed — followed by other {@code ACTIVE} products in the categories the user
     * has shown interest in (via orders, wishlist or cart). The list is de-duplicated and
     * capped at {@value #MAX_RECOMMENDATIONS} of the most recently updated products.</p>
     *
     * <p>Runs in a read-only transaction so the order → items → product relations can be
     * traversed lazily.</p>
     *
     * @return suggested active products (never {@code null})
     */
    @Transactional(readOnly = true)
    public List<Product> recommendForCurrentUser() {
        User user = currentUserProvider.getCurrentUser();

        Set<String> likedCategories = new HashSet<>();
        // Preserves insertion order so interest items (added first) stay ahead of category fills.
        LinkedHashMap<Long, Product> recommendations = new LinkedHashMap<>();

        // Items the user interacted with (cart/wishlist), most recent first. Kept after removal.
        for (Long productId : interestBean.getRecentProductIds()) {
            productRepository.findById(productId).ifPresent(product -> {
                if (product.getCategory() != null) {
                    likedCategories.add(product.getCategory());
                }
                if ("ACTIVE".equals(product.getStatus())) {
                    recommendations.putIfAbsent(product.getId(), product);
                }
            });
        }

        // Categories from past orders broaden the suggestions.
        for (Order order : orderService.findByBuyer(user)) {
            for (OrderItem item : order.getOrderItems()) {
                if (item.getProduct().getCategory() != null) {
                    likedCategories.add(item.getProduct().getCategory());
                }
            }
        }

        // Categories from the current wishlist and cart (covers items added before tracking).
        for (WishlistItem wishlistItem : wishlistService.findForCurrentUser()) {
            if (wishlistItem.getProduct().getCategory() != null) {
                likedCategories.add(wishlistItem.getProduct().getCategory());
            }
        }
        for (Long productId : cartBean.getItems().keySet()) {
            productRepository.findById(productId)
                    .filter(product -> product.getCategory() != null)
                    .ifPresent(product -> likedCategories.add(product.getCategory()));
        }

        // Fill remaining slots with other active products in those categories, newest first.
        if (!likedCategories.isEmpty()) {
            productRepository.findByCategoryInAndStatus(likedCategories, "ACTIVE").stream()
                    .sorted(Comparator.comparing(Product::getId).reversed())
                    .forEach(product -> recommendations.putIfAbsent(product.getId(), product));
        }

        return recommendations.values().stream()
                .limit(MAX_RECOMMENDATIONS)
                .toList();
    }

    /**
     * Returns other active products in the same category as the given product (excluding it).
     *
     * @param product the product being viewed
     * @return related active products (never {@code null})
     */
    public List<Product> relatedProducts(Product product) {
        if (product.getCategory() == null) {
            return List.of();
        }
        return productRepository.findByCategoryAndStatus(product.getCategory(), "ACTIVE").stream()
                .filter(other -> !other.getId().equals(product.getId()))
                .toList();
    }
}
