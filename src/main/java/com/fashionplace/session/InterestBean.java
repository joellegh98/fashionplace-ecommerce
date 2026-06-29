package com.fashionplace.session;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * Session-scoped memory of products the user has shown interest in (added to cart or wishlist).
 *
 * <p>Unlike the cart and wishlist, entries are kept even after the user removes the item, so
 * recommendations can keep suggesting it. Most recently touched products come first.</p>
 *
 * <p>Must implement {@link Serializable} for HTTP session storage.</p>
 */
public class InterestBean implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Product ids in insertion order; re-recording moves an id to the most-recent end. */
    private final LinkedHashSet<Long> productIds = new LinkedHashSet<>();

    /**
     * Records interest in a product, marking it as the most recently touched.
     *
     * @param productId the product the user interacted with
     */
    public synchronized void record(Long productId) {
        if (productId == null) {
            return;
        }
        productIds.remove(productId);
        productIds.add(productId);
    }

    /**
     * Returns the interested-in product ids, most recent first.
     *
     * @return recent product ids (newest interaction first)
     */
    public synchronized List<Long> getRecentProductIds() {
        List<Long> ordered = new ArrayList<>(productIds);
        Collections.reverse(ordered);
        return ordered;
    }
}
