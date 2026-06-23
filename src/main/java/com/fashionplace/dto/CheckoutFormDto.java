package com.fashionplace.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Form backing object for the checkout page: captures where the order ships.
 */
public class CheckoutFormDto {

    @NotBlank(message = "Please enter a shipping address.")
    private String shippingAddress;

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }
}
