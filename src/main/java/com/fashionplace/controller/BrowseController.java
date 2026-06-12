package com.fashionplace.controller;

import com.fashionplace.repository.ProductRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Serves the public Browse page that lists every product.
 *
 * <p>For now the controller talks to {@link ProductRepository} directly; a service layer
 * is introduced in a later phase.</p>
 */
@Controller
public class BrowseController {

    private final ProductRepository productRepository;

    public BrowseController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * Loads all products and renders them as cards on the Browse page.
     *
     * @param model holds the product list passed to the view
     * @return the {@code browse} view name
     */
    @GetMapping("/browse")
    public String browse(Model model) {
        model.addAttribute("products", productRepository.findAll());
        return "browse";
    }
}
