package com.fashionplace.controller;

import com.fashionplace.model.Product;
import com.fashionplace.service.CurrentUserProvider;
import com.fashionplace.service.ProductService;
import com.fashionplace.service.ReviewService;
import com.fashionplace.web.AddToCartForm;
import com.fashionplace.web.ReviewForm;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Serves the product detail page for a single item.
 */
@Controller
public class ProductController {

    private final ProductService productService;
    private final ReviewService reviewService;
    private final CurrentUserProvider currentUserProvider;

    public ProductController(ProductService productService,
                             ReviewService reviewService,
                             CurrentUserProvider currentUserProvider) {
        this.productService = productService;
        this.reviewService = reviewService;
        this.currentUserProvider = currentUserProvider;
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
        populateProductDetail(id, model, new ReviewForm());
        return "product";
    }

    /**
     * Saves a review submitted from the product detail page (attributed to the fake current user).
     *
     * @param id                 the product id from the URL
     * @param review             rating and comment from the form
     * @param bindingResult      validation errors, if any
     * @param model              repopulated when validation fails
     * @param redirectAttributes flash message on success
     * @return redirect to the product page, or the form again on error
     */
    @PostMapping("/product/{id}/review")
    public String submitReview(@PathVariable Long id,
                               @Valid @ModelAttribute("review") ReviewForm review,
                               BindingResult bindingResult,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            populateProductDetail(id, model, review);
            return "product";
        }

        Product product = productService.findById(id);
        reviewService.addReview(
                product,
                currentUserProvider.getCurrentUser(),
                review.getRating(),
                review.getComment()
        );
        redirectAttributes.addFlashAttribute("reviewMessage", "Thank you for your review!");
        return "redirect:/product/" + id;
    }

    private void populateProductDetail(Long id, Model model, ReviewForm reviewForm) {
        Product product = productService.findById(id);
        model.addAttribute("product", product);
        model.addAttribute("reviews", reviewService.findByProduct(product));
        model.addAttribute("averageRating", reviewService.averageRating(product));
        model.addAttribute("review", reviewForm);
        model.addAttribute("currentUsername", currentUserProvider.getCurrentUser().getUsername());

        AddToCartForm addToCart = new AddToCartForm();
        addToCart.setProductId(id);
        model.addAttribute("addToCart", addToCart);
    }
}
