package com.fashionplace.controller;

import com.fashionplace.model.Product;
import com.fashionplace.service.CurrentUserProvider;
import com.fashionplace.service.ProductService;
import com.fashionplace.service.RecommendationService;
import com.fashionplace.service.ReviewService;
import com.fashionplace.service.WishlistService;
import com.fashionplace.dto.AddToCartFormDto;
import com.fashionplace.dto.ReviewFormDto;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
/**
 * Serves the product detail page for a single item.
 */
@Controller
public class ProductController {

    private final ProductService productService;
    private final ReviewService reviewService;
    private final CurrentUserProvider currentUserProvider;
    private final WishlistService wishlistService;
    private final RecommendationService recommendationService;

    public ProductController(ProductService productService,
                             ReviewService reviewService,
                             CurrentUserProvider currentUserProvider,
                             WishlistService wishlistService,
                             RecommendationService recommendationService) {
        this.productService = productService;
        this.reviewService = reviewService;
        this.currentUserProvider = currentUserProvider;
        this.wishlistService = wishlistService;
        this.recommendationService = recommendationService;
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
        if (product.isDeleted() || "FLAGGED".equals(product.getStatus())) {
            return "redirect:/browse";
        }
        populateProductDetail(id, model, new ReviewFormDto());
        return "product";
    }

    /**
     * Saves a review submitted from the product detail page.
     *
     * @param id                 the product id from the URL
     * @param review             rating and comment from the form
     * @param bindingResult      validation errors, if any
     * @param model              repopulated when validation fails
     * @return redirect to the product page, or the form again on error
     */
    @PostMapping("/product/{id}/review")
    public String submitReview(@PathVariable Long id,
                               @Valid @ModelAttribute("review") ReviewFormDto review,
                               BindingResult bindingResult,
                               Model model) {
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
        return "redirect:/product/" + id;
    }

    private void populateProductDetail(Long id, Model model, ReviewFormDto reviewForm) {
        Product product = productService.findById(id);
        model.addAttribute("product", product);
        model.addAttribute("reviews", reviewService.findByProduct(product));
        model.addAttribute("averageRating", reviewService.averageRating(product));
        model.addAttribute("review", reviewForm);
        currentUserProvider.getCurrentUserOptional().ifPresent(user -> {
            model.addAttribute("currentUsername", user.getUsername());
            boolean ownProduct = product.getSeller() != null
                    && product.getSeller().getId().equals(user.getId());
            model.addAttribute("ownProduct", ownProduct);
        });
        if (!model.containsAttribute("ownProduct")) {
            model.addAttribute("ownProduct", false);
        }
        model.addAttribute("onWishlist", wishlistService.isOnCurrentUserWishlist(product));
        model.addAttribute("relatedProducts", recommendationService.relatedProducts(product));

        AddToCartFormDto addToCart = new AddToCartFormDto();
        addToCart.setProductId(id);
        model.addAttribute("addToCart", addToCart);
    }
}
