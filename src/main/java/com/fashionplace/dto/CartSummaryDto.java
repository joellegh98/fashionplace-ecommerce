package com.fashionplace.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

/**
 * Resolved cart contents for display: line items, grand total, and any product ids
 * that were in the session cart but no longer exist in the database.
 */
public class CartSummaryDto {

    @NotNull(message = "Cart lines are required.")
    @Valid
    private final List<CartLineItemDto> lines;

    @NotNull(message = "Grand total is required.")
    @DecimalMin(value = "0.00", message = "Grand total cannot be negative.")
    private final BigDecimal grandTotal;

    @NotNull(message = "Missing product ids list is required.")
    private final List<Long> missingProductIds;

    public CartSummaryDto(List<CartLineItemDto> lines, BigDecimal grandTotal, List<Long> missingProductIds) {
        this.lines = lines;
        this.grandTotal = grandTotal;
        this.missingProductIds = missingProductIds;
    }

    public List<CartLineItemDto> getLines() {
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
