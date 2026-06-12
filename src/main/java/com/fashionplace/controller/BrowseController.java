package com.fashionplace.controller;

import com.fashionplace.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Serves the public Browse page that lists every product.
 */
@Controller
public class BrowseController {

    private final ProductService productService;

    public BrowseController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * Loads all products and renders them as cards on the Browse page.
     *
     * @param model holds the product list passed to the view
     * @return the {@code browse} view name
     */
    @GetMapping("/browse")
    public String browse(Model model) {
        model.addAttribute("products", productService.findAll());
        return "browse";
    }
}
