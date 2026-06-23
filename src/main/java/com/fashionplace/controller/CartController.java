package com.fashionplace.controller;

import com.fashionplace.model.Product;
import com.fashionplace.service.CurrentUserProvider;
import com.fashionplace.service.ProductService;
import com.fashionplace.session.CartBean;
import com.fashionplace.session.InterestBean;
import com.fashionplace.dto.AddToCartFormDto;
import com.fashionplace.dto.CartSummaryDto;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

/**
 * Handles shopping-cart mutations (add, update, remove).
 */
@Controller
public class CartController {

    private final ProductService productService;
    private final CurrentUserProvider currentUserProvider;

    @Resource
    private CartBean cartBean;

    @Resource
    private InterestBean interestBean;

    public CartController(ProductService productService,
                          CurrentUserProvider currentUserProvider) {
        this.productService = productService;
        this.currentUserProvider = currentUserProvider;
    }

    /**
     * Shows the session cart with line totals and a grand total.
     *
     * @param model holds {@code cartItems} and {@code grandTotal} for the view
     * @return the {@code cart} view name
     */
    @GetMapping("/cart")
    public String cart(Model model) {
        addCartToModel(model);
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
    public String addToCart(@Valid @ModelAttribute AddToCartFormDto form,
                            BindingResult bindingResult,
                            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", firstError(bindingResult));
            return "redirect:/browse";
        }
        Optional<Product> productOpt = productService.findByIdOptional(form.getProductId());
        if (productOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "This item is no longer available.");
            return "redirect:/browse";
        }
        Product product = productOpt.get();
        int alreadyInCart = cartBean.getItems().getOrDefault(form.getProductId(), 0);
        int requestedTotal = alreadyInCart + Math.max(form.getQuantity(), 0);

        if (isOwnProduct(product)) {
            redirectAttributes.addFlashAttribute("errorMessage", "You can't buy your own listing.");
        } else if (product.getQuantity() <= 0) {
            redirectAttributes.addFlashAttribute("errorMessage", "This item is out of stock.");
        } else if (requestedTotal > product.getQuantity()) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Only " + product.getQuantity() + " in stock"
                            + (alreadyInCart > 0 ? " (you already have " + alreadyInCart + " in your cart)." : "."));
        } else {
            cartBean.addItem(form.getProductId(), form.getQuantity());
            interestBean.record(form.getProductId());
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
    public String updateQuantity(@Valid @ModelAttribute AddToCartFormDto form,
                                 BindingResult bindingResult,
                                 RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", firstError(bindingResult));
            return "redirect:/cart";
        }
        Optional<Product> productOpt = productService.findByIdOptional(form.getProductId());
        if (productOpt.isEmpty()) {
            cartBean.removeItem(form.getProductId());
            redirectAttributes.addFlashAttribute("errorMessage", unavailableCartMessage());
            return "redirect:/cart";
        }
        Product product = productOpt.get();
        if (form.getQuantity() > product.getQuantity()) {
            cartBean.setQuantity(form.getProductId(), product.getQuantity());
            redirectAttributes.addFlashAttribute("errorMessage",
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

    /** Resolves cart lines, drops deleted products from the session cart, and fills the model. */
    private void addCartToModel(Model model) {
        CartSummaryDto summary = productService.summarizeCart(cartBean.getItems());
        summary.getMissingProductIds().forEach(cartBean::removeItem);
        if (summary.hadMissingProducts()) {
            model.addAttribute("errorMessage", unavailableCartMessage());
        }
        model.addAttribute("cartItems", summary.getLines());
        model.addAttribute("grandTotal", summary.getGrandTotal());
    }

    /** Whether the given product was listed by the current user. */
    private boolean isOwnProduct(Product product) {
        return currentUserProvider.getCurrentUserOptional()
                .map(user -> product.getSeller() != null
                        && product.getSeller().getId().equals(user.getId()))
                .orElse(false);
    }

    private static String unavailableCartMessage() {
        return "One or more items were removed from your cart because they are no longer available.";
    }

    /** Returns a user-friendly message for the first binding error. */
    private static String firstError(BindingResult bindingResult) {
        return bindingResult.getAllErrors().stream()
                .findFirst()
                .map(error -> error.getDefaultMessage())
                .orElse("Invalid request.");
    }
}
