package com.fashionplace.controller;

import com.fashionplace.model.Order;
import com.fashionplace.model.Product;
import com.fashionplace.service.CurrentUserProvider;
import com.fashionplace.service.OrderService;
import com.fashionplace.service.ProductService;
import com.fashionplace.session.CartBean;
import com.fashionplace.web.CartLineItem;
import com.fashionplace.web.CheckoutForm;
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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Shows the checkout page (cart summary + shipping address) and places the order.
 */
@Controller
public class CheckoutController {

    private final ProductService productService;
    private final CurrentUserProvider currentUserProvider;
    private final OrderService orderService;

    @Resource
    private CartBean cartBean;

    public CheckoutController(ProductService productService,
                             CurrentUserProvider currentUserProvider,
                             OrderService orderService) {
        this.productService = productService;
        this.currentUserProvider = currentUserProvider;
        this.orderService = orderService;
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

        CheckoutForm checkoutForm = new CheckoutForm();
        checkoutForm.setShippingAddress(currentUserProvider.getCurrentUser().getAddress());
        model.addAttribute("checkoutForm", checkoutForm);
        addCartSummary(model);
        return "checkout";
    }

    /**
     * Places the order from the current cart, atomically creating the order and its lines.
     *
     * @param checkoutForm  the shipping address
     * @param bindingResult validation errors, if any
     * @param model         repopulated with the cart summary when validation fails
     * @return redirect to the confirmation page, or the checkout form again on error
     */
    @PostMapping("/checkout")
    public String placeOrder(@Valid @ModelAttribute("checkoutForm") CheckoutForm checkoutForm,
                             BindingResult bindingResult,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        if (cartBean.isEmpty()) {
            return "redirect:/cart";
        }
        if (bindingResult.hasErrors()) {
            addCartSummary(model);
            return "checkout";
        }

        String stockProblem = findStockProblem();
        if (stockProblem != null) {
            redirectAttributes.addFlashAttribute("cartError", stockProblem);
            return "redirect:/cart";
        }

        Order order = orderService.placeOrder(
                currentUserProvider.getCurrentUser(),
                cartBean.getItems(),
                checkoutForm.getShippingAddress());
        cartBean.clear();
        return "redirect:/checkout/confirmation/" + order.getId();
    }

    /**
     * Checks the cart against current stock.
     *
     * @return an error message if any line exceeds available stock, otherwise {@code null}
     */
    private String findStockProblem() {
        for (Map.Entry<Long, Integer> entry : cartBean.getItems().entrySet()) {
            Product product = productService.findById(entry.getKey());
            if (entry.getValue() > product.getQuantity()) {
                return "Not enough stock for \"" + product.getTitle()
                        + "\" (only " + product.getQuantity() + " left). Please update your cart.";
            }
        }
        return null;
    }

    /**
     * Shows a confirmation for a placed order.
     *
     * @param id    the order id
     * @param model holds the {@code order} for the view
     * @return the {@code order-confirmation} view name
     */
    @GetMapping("/checkout/confirmation/{id}")
    public String confirmation(@PathVariable Long id, Model model) {
        model.addAttribute("order", orderService.findById(id));
        return "order-confirmation";
    }

    /** Adds {@code cartItems} and {@code grandTotal} model attributes from the session cart. */
    private void addCartSummary(Model model) {
        List<CartLineItem> cartItems = new ArrayList<>();
        BigDecimal grandTotal = BigDecimal.ZERO;
        for (Map.Entry<Long, Integer> entry : cartBean.getItems().entrySet()) {
            Product product = productService.findById(entry.getKey());
            CartLineItem line = new CartLineItem(
                    product.getId(), product.getTitle(), product.getPrice(), entry.getValue());
            cartItems.add(line);
            grandTotal = grandTotal.add(line.getLineTotal());
        }
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("grandTotal", grandTotal);
    }
}
