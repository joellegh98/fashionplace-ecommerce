package com.fashionplace.session;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Session-scoped shopping cart. Holds {@code productId → quantity} pairs for the
 * current browser session (no login required until Phase 9).
 *
 * <p>Must implement {@link Serializable} for HTTP session storage.</p>
 */
public class CartBean implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Product id → quantity (insertion order preserved for display). */
    private final Map<Long, Integer> items = new LinkedHashMap<>();

    /**
     * Adds a product to the cart or increases its quantity.
     *
     * @param productId the product to add
     * @param quantity  how many to add (ignored if less than 1)
     */
    public void addItem(Long productId, int quantity) {
        if (productId == null || quantity < 1) {
            return;
        }
        items.merge(productId, quantity, Integer::sum);
    }

    /**
     * Sets the quantity for a product. A quantity of zero or less removes the line.
     *
     * @param productId the product
     * @param quantity  the new quantity
     */
    public void setQuantity(Long productId, int quantity) {
        if (productId == null) {
            return;
        }
        if (quantity < 1) {
            items.remove(productId);
        } else {
            items.put(productId, quantity);
        }
    }

    /**
     * Removes a product from the cart entirely.
     *
     * @param productId the product to remove
     */
    public void removeItem(Long productId) {
        if (productId != null) {
            items.remove(productId);
        }
    }

    /**
     * Returns all cart lines as an unmodifiable map (product id → quantity).
     */
    public Map<Long, Integer> getItems() {
        return Collections.unmodifiableMap(items);
    }

    /**
     * Total number of units across all lines (sum of quantities).
     */
    public int getTotalItemCount() {
        return items.values().stream().mapToInt(Integer::intValue).sum();
    }

    /**
     * Number of distinct products in the cart.
     */
    public int getDistinctItemCount() {
        return items.size();
    }

    /**
     * Whether the cart has no items.
     */
    public boolean isEmpty() {
        return items.isEmpty();
    }

    /**
     * Removes every item from the cart.
     */
    public void clear() {
        items.clear();
    }
}
