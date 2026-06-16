package com.fashionplace.controller;

import com.fashionplace.model.Product;
import com.fashionplace.service.CurrentUserProvider;
import com.fashionplace.service.ProductService;
import com.fashionplace.web.SellForm;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

/**
 * Lets the current (fake) user list a new product for sale.
 *
 * <p>The created product is owned by the current user. Server-side validation on
 * {@link SellForm} rejects invalid submissions before persisting.</p>
 */
@Controller
public class SellController {

    private final ProductService productService;
    private final CurrentUserProvider currentUserProvider;

    public SellController(ProductService productService,
                          CurrentUserProvider currentUserProvider) {
        this.productService = productService;
        this.currentUserProvider = currentUserProvider;
    }

    /**
     * Shows the empty sell form.
     *
     * @param model holds the {@code sellForm} and dropdown options
     * @return the {@code sell} view name
     */
    @GetMapping("/sell")
    public String sellForm(Model model) {
        model.addAttribute("sellForm", new SellForm());
        addFormOptions(model);
        return "sell";
    }

    /**
     * Creates a new product owned by the current user, then redirects to its detail page.
     *
     * @param sellForm      the submitted listing details
     * @param bindingResult validation errors, if any
     * @param model         repopulated when validation fails
     * @return redirect to the new product's detail page, or the form again on error
     */
    @PostMapping("/sell")
    public String createListing(@Valid @ModelAttribute("sellForm") SellForm sellForm,
                                BindingResult bindingResult,
                                Model model) {
        if (bindingResult.hasErrors()) {
            addFormOptions(model);
            return "sell";
        }
        Product product = productService.createListing(sellForm, currentUserProvider.getCurrentUser());
        return "redirect:/product/" + product.getId();
    }

    /** Adds the category and condition options shown in the form dropdowns. */
    private void addFormOptions(Model model) {
        model.addAttribute("categoryOptions", List.of("Jewelry", "Clothing"));
        model.addAttribute("conditionOptions", List.of("New", "Used"));
    }
}
