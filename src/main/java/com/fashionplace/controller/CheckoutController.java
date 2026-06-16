package com.fashionplace.controller;

import com.fashionplace.model.Order;
import com.fashionplace.model.Product;
import com.fashionplace.model.User;
import com.fashionplace.service.CurrentUserProvider;
import com.fashionplace.service.OrderService;
import com.fashionplace.service.ProductService;
import com.fashionplace.session.CartBean;
import com.fashionplace.web.CartSummary;
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
    public String checkout(Model model, RedirectAttributes redirectAttributes) {
        if (cartBean.isEmpty()) {
            return "redirect:/cart";
        }
        if (!addCartSummary(model, redirectAttributes)) {
            return "redirect:/cart";
        }

        CheckoutForm checkoutForm = new CheckoutForm();
        checkoutForm.setShippingAddress(currentUserProvider.getCurrentUser().getAddress());
        model.addAttribute("checkoutForm", checkoutForm);
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
            addCartSummary(model, null);
            return "checkout";
        }

        String unavailable = removeUnavailableCartItems();
        if (unavailable != null) {
            redirectAttributes.addFlashAttribute("cartError", unavailable);
            return "redirect:/cart";
        }
        if (cartBean.isEmpty()) {
            redirectAttributes.addFlashAttribute("cartError", unavailableCartMessage());
            return "redirect:/cart";
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
        User currentUser = currentUserProvider.getCurrentUser();
        for (Map.Entry<Long, Integer> entry : cartBean.getItems().entrySet()) {
            Product product = productService.findByIdOptional(entry.getKey()).orElse(null);
            if (product == null) {
                return unavailableCartMessage();
            }
            if (product.getSeller() != null
                    && product.getSeller().getId().equals(currentUser.getId())) {
                return "You can't buy your own listing (\"" + product.getTitle()
                        + "\"). Please remove it from your cart.";
            }
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

    /**
     * Adds {@code cartItems} and {@code grandTotal} to the model, pruning deleted products
     * from the session cart.
     *
     * @return {@code false} when items were removed and the caller should redirect to the cart
     */
    private boolean addCartSummary(Model model, RedirectAttributes redirectAttributes) {
        CartSummary summary = productService.summarizeCart(cartBean.getItems());
        summary.getMissingProductIds().forEach(cartBean::removeItem);
        if (summary.hadMissingProducts()) {
            if (redirectAttributes != null) {
                redirectAttributes.addFlashAttribute("cartError", unavailableCartMessage());
            } else {
                model.addAttribute("cartError", unavailableCartMessage());
            }
            return false;
        }
        model.addAttribute("cartItems", summary.getLines());
        model.addAttribute("grandTotal", summary.getGrandTotal());
        return true;
    }

    /** Removes unavailable products from the cart; returns a message if any were removed. */
    private String removeUnavailableCartItems() {
        CartSummary summary = productService.summarizeCart(cartBean.getItems());
        summary.getMissingProductIds().forEach(cartBean::removeItem);
        return summary.hadMissingProducts() ? unavailableCartMessage() : null;
    }

    private static String unavailableCartMessage() {
        return "One or more items were removed from your cart because they are no longer available.";
    }
}
