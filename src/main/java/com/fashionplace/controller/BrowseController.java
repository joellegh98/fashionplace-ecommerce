package com.fashionplace.controller;

import com.fashionplace.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

/**
 * Serves the public Browse page with keyword search and optional filters.
 */
@Controller
public class BrowseController {

    private final ProductService productService;

    public BrowseController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * Lists products as cards, applying optional title search and browse filters.
     *
     * @param q         optional title keyword
     * @param category  optional category filter
     * @param condition optional condition filter
     * @param minPrice  optional minimum price
     * @param maxPrice  optional maximum price
     * @param model     holds products and current filter state for the view
     * @return the {@code browse} view name
     */
    @GetMapping("/browse")
    public String browse(@RequestParam(required = false, defaultValue = "") String q,
                         @RequestParam(required = false, defaultValue = "") String category,
                         @RequestParam(required = false, defaultValue = "") String condition,
                         @RequestParam(required = false) BigDecimal minPrice,
                         @RequestParam(required = false) BigDecimal maxPrice,
                         Model model) {
        model.addAttribute("products",
                productService.browse(q, category, condition, minPrice, maxPrice));
        model.addAttribute("q", q);
        model.addAttribute("category", category);
        model.addAttribute("condition", condition);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
        model.addAttribute("categories", productService.findDistinctCategories());
        model.addAttribute("conditions", productService.findDistinctConditions());
        return "browse";
    }
}
