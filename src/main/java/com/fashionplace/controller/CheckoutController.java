package com.fashionplace.controller;

import com.fashionplace.model.Product;
import com.fashionplace.service.CurrentUserProvider;
import com.fashionplace.service.ProductService;
import com.fashionplace.session.CartBean;
import com.fashionplace.web.CartLineItem;
import com.fashionplace.web.CheckoutForm;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Shows the checkout page: a read-only cart summary plus a shipping address field.
 * Placing the order (POST) arrives in Phase 5.3.
 */
@Controller
public class CheckoutController {

    private final ProductService productService;
    private final CurrentUserProvider currentUserProvider;

    @Resource
    private CartBean cartBean;

    public CheckoutController(ProductService productService, CurrentUserProvider currentUserProvider) {
        this.productService = productService;
        this.currentUserProvider = currentUserProvider;
    }

    /**
     * Renders the checkout summary. Redirects to the cart if it is empty (nothing to check out).
     * The shipping address is pre-filled from the current user's saved address.
     *
     * @param model holds {@code cartItems}, {@code grandTotal}, and the {@code checkoutForm}
     * @return the {@code checkout} view name, or a redirect to the cart when empty
     */
    @GetMapping("/checkout")
    public String checkout(Model model) {
        if (cartBean.isEmpty()) {
            return "redirect:/cart";
        }

        List<CartLineItem> cartItems = new ArrayList<>();
        BigDecimal grandTotal = BigDecimal.ZERO;
        for (Map.Entry<Long, Integer> entry : cartBean.getItems().entrySet()) {
            Product product = productService.findById(entry.getKey());
            CartLineItem line = new CartLineItem(
                    product.getId(), product.getTitle(), product.getPrice(), entry.getValue());
            cartItems.add(line);
            grandTotal = grandTotal.add(line.getLineTotal());
        }

        CheckoutForm checkoutForm = new CheckoutForm();
        checkoutForm.setShippingAddress(currentUserProvider.getCurrentUser().getAddress());

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("grandTotal", grandTotal);
        model.addAttribute("checkoutForm", checkoutForm);
        return "checkout";
    }
}
