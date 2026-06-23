package com.fashionplace.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * Form backing object for the "Add to Cart" POST on the product detail page.
 */
public class AddToCartFormDto {

    @NotNull(message = "Please choose a product.")
    private Long productId;

    @Min(value = 1, message = "Quantity must be at least 1.")
    private int quantity = 1;

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
