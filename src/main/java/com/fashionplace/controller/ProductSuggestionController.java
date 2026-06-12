package com.fashionplace.controller;

import com.fashionplace.service.ProductService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * JSON endpoint backing the Browse search autocomplete. Returns product titles that
 * match the typed text so the browser can show them in a {@code <datalist>}.
 */
@RestController
public class ProductSuggestionController {

    private final ProductService productService;

    public ProductSuggestionController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * Returns product titles matching the given keyword.
     *
     * @param q the partial title typed by the user
     * @return matching titles as a JSON array of strings
     */
    @GetMapping("/api/products/suggest")
    public List<String> suggest(@RequestParam(required = false, defaultValue = "") String q) {
        return productService.suggestTitles(q);
    }
}
