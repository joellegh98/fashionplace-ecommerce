package com.fashionplace.web;

/**
 * Form backing object for the "Add to Cart" POST on the product detail page.
 */
public class AddToCartForm {

    private Long productId;
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
