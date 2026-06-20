package com.fashionplace.controller;

import com.fashionplace.model.Product;
import com.fashionplace.model.ProductCategories;
import com.fashionplace.service.CurrentUserProvider;
import com.fashionplace.service.ProductService;
import com.fashionplace.web.SellForm;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * Lets the signed-in user list and manage their products for sale.
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

    /**
     * Lists the current user's products (active and sold), newest first.
     *
     * @param model holds {@code myProducts} for the view
     * @return the {@code my-products} view name
     */
    @GetMapping("/my-products")
    public String myProducts(Model model) {
        model.addAttribute("myProducts",
                productService.findBySeller(currentUserProvider.getCurrentUser()));
        return "my-products";
    }

    /**
     * Shows the edit form for an existing product, pre-filled with its current values.
     *
     * @param id    the product to edit
     * @param model holds the {@code sellForm}, {@code productId}, {@code product}, and options
     * @return the {@code edit-product} view name
     */
    @GetMapping("/product/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Product product = productService.findOwnedListing(id, currentUserProvider.getCurrentUser());
        model.addAttribute("sellForm", productService.toForm(product));
        model.addAttribute("product", product);
        model.addAttribute("productId", id);
        addFormOptions(model);
        return "edit-product";
    }

    /**
     * Saves edits to an existing product, then redirects to its detail page.
     *
     * @param id            the product being edited
     * @param sellForm      the submitted edit details
     * @param bindingResult validation errors, if any
     * @param model         repopulated when validation fails
     * @return redirect to the product detail page, or the edit form again on error
     */
    @PostMapping("/product/{id}/edit")
    public String updateListing(@PathVariable Long id,
                                @Valid @ModelAttribute("sellForm") SellForm sellForm,
                                BindingResult bindingResult,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("product",
                    productService.findOwnedListing(id, currentUserProvider.getCurrentUser()));
            model.addAttribute("productId", id);
            addFormOptions(model);
            return "edit-product";
        }
        productService.updateListing(id, sellForm, currentUserProvider.getCurrentUser());
        redirectAttributes.addFlashAttribute("successMessage", "Product updated.");
        return "redirect:/product/" + id;
    }

    /**
     * Deletes a product and returns to My Products.
     *
     * @param id                 the product to delete
     * @param redirectAttributes flash message shown after the redirect
     * @return redirect to My Products
     */
    @PostMapping("/product/{id}/delete")
    public String deleteListing(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            productService.deleteOwnedListing(id, currentUserProvider.getCurrentUser());
            redirectAttributes.addFlashAttribute("successMessage", "Product deleted.");
        } catch (DataIntegrityViolationException e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "This product could not be deleted due to a database constraint.");
        }
        return "redirect:/my-products";
    }

    /** Adds the category and condition options shown in the form dropdowns. */
    private void addFormOptions(Model model) {
        model.addAttribute("categoryOptions", ProductCategories.ALL);
        model.addAttribute("conditionOptions", List.of("New", "Used"));
    }
}
