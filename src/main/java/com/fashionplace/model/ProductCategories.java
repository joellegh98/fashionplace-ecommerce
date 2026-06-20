package com.fashionplace.model;

import java.util.List;

/**
 * Allowed product categories for listings and browse filters.
 */
public final class ProductCategories {

    public static final List<String> ALL = List.of(
            "Earrings",
            "Bracelet",
            "Necklace",
            "Ring",
            "Shirts",
            "Bottoms",
            "Jeans",
            "Underwear",
            "Shoes",
            "Glasses"
    );

    private ProductCategories() {
    }
}
