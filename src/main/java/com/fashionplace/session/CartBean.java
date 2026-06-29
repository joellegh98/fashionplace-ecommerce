package com.fashionplace.session;

import java.io.Serializable;
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
    public synchronized void addItem(Long productId, int quantity) {
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
    public synchronized void setQuantity(Long productId, int quantity) {
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
    public synchronized void removeItem(Long productId) {
        if (productId != null) {
            items.remove(productId);
        }
    }

    /**
     * Returns a snapshot of all cart lines (product id → quantity). Callers can safely
     * iterate without holding the cart lock.
     */
    public synchronized Map<Long, Integer> getItems() {
        return Map.copyOf(items);
    }

    /**
     * Atomically copies and clears the cart. Used at checkout to prevent duplicate orders
     * when the user double-submits or opens parallel requests in the same session.
     *
     * @return the cart contents before clearing, or an empty map if the cart was already empty
     */
    public synchronized Map<Long, Integer> drainItems() {
        if (items.isEmpty()) {
            return Map.of();
        }
        Map<Long, Integer> snapshot = Map.copyOf(items);
        items.clear();
        return snapshot;
    }

    /**
     * Restores cart lines after a failed checkout attempt.
     *
     * @param snapshot lines to put back; merged with any items added while checkout was in flight
     */
    public synchronized void restoreItems(Map<Long, Integer> snapshot) {
        if (snapshot == null || snapshot.isEmpty()) {
            return;
        }
        for (Map.Entry<Long, Integer> entry : snapshot.entrySet()) {
            items.merge(entry.getKey(), entry.getValue(), Integer::sum);
        }
    }

    /**
     * Total number of units across all lines (sum of quantities).
     */
    public synchronized int getTotalItemCount() {
        return items.values().stream().mapToInt(Integer::intValue).sum();
    }

    /**
     * Number of distinct products in the cart.
     */
    public synchronized int getDistinctItemCount() {
        return items.size();
    }

    /**
     * Whether the cart has no items.
     */
    public synchronized boolean isEmpty() {
        return items.isEmpty();
    }

    /**
     * Removes every item from the cart.
     */
    public synchronized void clear() {
        items.clear();
    }
}
