package com.fashionplace.controller;

import com.fashionplace.service.ProductService;
import com.fashionplace.session.CartBean;
import com.fashionplace.web.AddToCartForm;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Handles shopping-cart mutations (add, update, remove).
 */
@Controller
public class CartController {

    private final ProductService productService;

    @Resource
    private CartBean cartBean;

    public CartController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * Adds a product to the session cart and redirects back to its detail page.
     *
     * @param form               product id and quantity from the form
     * @param redirectAttributes flash message shown after redirect
     * @return redirect to the product detail page
     */
    @PostMapping("/cart/add")
    public String addToCart(@ModelAttribute AddToCartForm form, RedirectAttributes redirectAttributes) {
        productService.findById(form.getProductId());
        cartBean.addItem(form.getProductId(), form.getQuantity());
        redirectAttributes.addFlashAttribute("cartMessage", "Added to cart.");
        return "redirect:/product/" + form.getProductId();
    }
}
