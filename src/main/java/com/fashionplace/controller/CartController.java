package com.fashionplace.controller;

import com.fashionplace.model.Product;
import com.fashionplace.service.ProductService;
import com.fashionplace.session.CartBean;
import com.fashionplace.session.InterestBean;
import com.fashionplace.web.AddToCartForm;
import com.fashionplace.web.CartLineItem;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Handles shopping-cart mutations (add, update, remove).
 */
@Controller
public class CartController {

    private final ProductService productService;

    @Resource
    private CartBean cartBean;

    @Resource
    private InterestBean interestBean;

    public CartController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * Shows the session cart with line totals and a grand total.
     *
     * @param model holds {@code cartItems} and {@code grandTotal} for the view
     * @return the {@code cart} view name
     */
    @GetMapping("/cart")
    public String cart(Model model) {
        List<CartLineItem> cartItems = new ArrayList<>();
        BigDecimal grandTotal = BigDecimal.ZERO;

        for (Map.Entry<Long, Integer> entry : cartBean.getItems().entrySet()) {
            Product product = productService.findById(entry.getKey());
            int quantity = entry.getValue();
            CartLineItem line = new CartLineItem(
                    product.getId(), product.getTitle(), product.getPrice(), quantity);
            cartItems.add(line);
            grandTotal = grandTotal.add(line.getLineTotal());
        }

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("grandTotal", grandTotal);
        return "cart";
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
        Product product = productService.findById(form.getProductId());
        int alreadyInCart = cartBean.getItems().getOrDefault(form.getProductId(), 0);
        int requestedTotal = alreadyInCart + Math.max(form.getQuantity(), 0);

        if (product.getQuantity() <= 0) {
            redirectAttributes.addFlashAttribute("cartError", "This item is out of stock.");
        } else if (requestedTotal > product.getQuantity()) {
            redirectAttributes.addFlashAttribute("cartError",
                    "Only " + product.getQuantity() + " in stock"
                            + (alreadyInCart > 0 ? " (you already have " + alreadyInCart + " in your cart)." : "."));
        } else {
            cartBean.addItem(form.getProductId(), form.getQuantity());
            interestBean.record(form.getProductId());
            redirectAttributes.addFlashAttribute("cartMessage", "Added to cart.");
        }
        return "redirect:/product/" + form.getProductId();
    }

    /**
     * Sets the quantity for a cart line (removes the line if quantity is below 1).
     *
     * @param form the product id and new quantity from the cart page form
     * @return redirect back to the cart page
     */
    @PostMapping("/cart/update")
    public String updateQuantity(@ModelAttribute AddToCartForm form, RedirectAttributes redirectAttributes) {
        Product product = productService.findById(form.getProductId());
        if (form.getQuantity() > product.getQuantity()) {
            cartBean.setQuantity(form.getProductId(), product.getQuantity());
            redirectAttributes.addFlashAttribute("cartError",
                    "Only " + product.getQuantity() + " of \"" + product.getTitle() + "\" in stock.");
        } else {
            cartBean.setQuantity(form.getProductId(), form.getQuantity());
        }
        return "redirect:/cart";
    }

    /**
     * Removes a product from the session cart.
     *
     * @param id the product id to remove
     * @return redirect back to the cart page
     */
    @PostMapping("/cart/remove/{id}")
    public String removeItem(@PathVariable Long id) {
        cartBean.removeItem(id);
        return "redirect:/cart";
    }
}
