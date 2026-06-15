package com.fashionplace.controller;

import com.fashionplace.model.Product;
import com.fashionplace.service.ProductService;
import com.fashionplace.service.ReviewService;
import com.fashionplace.web.AddToCartForm;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Serves the product detail page for a single item.
 */
@Controller
public class ProductController {

    private final ProductService productService;
    private final ReviewService reviewService;

    public ProductController(ProductService productService, ReviewService reviewService) {
        this.productService = productService;
        this.reviewService = reviewService;
    }

    /**
     * Loads one product by id and renders its full details.
     *
     * @param id    the product id from the URL
     * @param model holds the product passed to the view
     * @return the {@code product} view name
     */
    @GetMapping("/product/{id}")
    public String productDetail(@PathVariable Long id, Model model) {
        Product product = productService.findById(id);
        model.addAttribute("product", product);
        model.addAttribute("reviews", reviewService.findByProduct(product));
        model.addAttribute("averageRating", reviewService.averageRating(product));

        AddToCartForm addToCart = new AddToCartForm();
        addToCart.setProductId(id);
        model.addAttribute("addToCart", addToCart);

        return "product";
    }
}
