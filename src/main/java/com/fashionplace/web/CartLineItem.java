package com.fashionplace.web;

import java.math.BigDecimal;

/**
 * One row on the cart page: product details plus quantity and line total.
 */
public class CartLineItem {

    private final Long id;
    private final String title;
    private final BigDecimal price;
    private final int quantity;
    private final BigDecimal lineTotal;

    public CartLineItem(Long id, String title, BigDecimal price, int quantity) {
        this.id = id;
        this.title = title;
        this.price = price;
        this.quantity = quantity;
        this.lineTotal = price.multiply(BigDecimal.valueOf(quantity));
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getLineTotal() {
        return lineTotal;
    }
}
