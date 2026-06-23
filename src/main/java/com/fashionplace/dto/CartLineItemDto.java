package com.fashionplace.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * One row on the cart page: product details plus quantity and line total.
 */
public class CartLineItemDto {

    @NotNull(message = "Product id is required.")
    @Positive(message = "Product id must be positive.")
    private final Long id;

    @NotBlank(message = "Title cannot be empty.")
    @Size(max = 255, message = "Title must be at most 255 characters.")
    private final String title;

    @NotNull(message = "Price is required.")
    @DecimalMin(value = "0.01", message = "Price must be at least $0.01.")
    private final BigDecimal price;

    @Min(value = 1, message = "Quantity must be at least 1.")
    private final int quantity;

    @NotNull(message = "Line total is required.")
    @DecimalMin(value = "0.01", message = "Line total must be at least $0.01.")
    private final BigDecimal lineTotal;

    public CartLineItemDto(Long id, String title, BigDecimal price, int quantity) {
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
