package com.fashionplace.web;

import java.math.BigDecimal;
import java.util.List;

/**
 * Resolved cart contents for display: line items, grand total, and any product ids
 * that were in the session cart but no longer exist in the database.
 */
public class CartSummary {

    private final List<CartLineItem> lines;
    private final BigDecimal grandTotal;
    private final List<Long> missingProductIds;

    public CartSummary(List<CartLineItem> lines, BigDecimal grandTotal, List<Long> missingProductIds) {
        this.lines = lines;
        this.grandTotal = grandTotal;
        this.missingProductIds = missingProductIds;
    }

    public List<CartLineItem> getLines() {
        return lines;
    }

    public BigDecimal getGrandTotal() {
        return grandTotal;
    }

    public List<Long> getMissingProductIds() {
        return missingProductIds;
    }

    public boolean hadMissingProducts() {
        return !missingProductIds.isEmpty();
    }
}
