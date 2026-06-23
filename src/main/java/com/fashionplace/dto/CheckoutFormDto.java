package com.fashionplace.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Form backing object for the checkout page: captures where the order ships.
 */
public class CheckoutFormDto {

    @NotBlank(message = "Please enter a shipping address.")
    @Size(min = 5, max = 500, message = "Shipping address must be between 5 and 500 characters.")
    private String shippingAddress;

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }
}
