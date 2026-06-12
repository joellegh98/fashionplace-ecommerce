package com.fashionplace.controller;

import com.fashionplace.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Serves the public Browse page that lists products, optionally filtered by a keyword.
 */
@Controller
public class BrowseController {

    private final ProductService productService;

    public BrowseController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * Lists products as cards, filtering by title when a search keyword is provided.
     *
     * @param q     optional title keyword from the query string (e.g. {@code /browse?q=ring})
     * @param model holds the product list and current keyword passed to the view
     * @return the {@code browse} view name
     */
    @GetMapping("/browse")
    public String browse(@RequestParam(required = false, defaultValue = "") String q,
                         Model model) {
        model.addAttribute("products", productService.search(q));
        model.addAttribute("q", q);
        return "browse";
    }
}
