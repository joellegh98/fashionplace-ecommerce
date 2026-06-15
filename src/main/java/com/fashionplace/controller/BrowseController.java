package com.fashionplace.controller;

import com.fashionplace.service.ProductService;
import com.fashionplace.service.RecommendationService;
import com.fashionplace.service.WishlistService;
import com.fashionplace.session.RecentSearchBean;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

/**
 * Serves the public Browse page with keyword search and optional filters.
 */
@Controller
public class BrowseController {

    private final ProductService productService;
    private final WishlistService wishlistService;
    private final RecommendationService recommendationService;

    @Resource
    private RecentSearchBean recentSearchBean;

    public BrowseController(ProductService productService,
                            WishlistService wishlistService,
                            RecommendationService recommendationService) {
        this.productService = productService;
        this.wishlistService = wishlistService;
        this.recommendationService = recommendationService;
    }

    /**
     * Lists products as cards, applying optional title search and browse filters.
     *
     * @param q         optional title keyword
     * @param category  optional category filter
     * @param condition optional condition filter
     * @param minPrice  optional minimum price
     * @param maxPrice  optional maximum price
     * @param sort      sort key: {@code newest}, {@code price_asc}, or {@code price_desc}
     * @param model     holds products and current filter state for the view
     * @return the {@code browse} view name
     */
    @GetMapping("/browse")
    public String browse(@RequestParam(required = false, defaultValue = "") String q,
                         @RequestParam(required = false, defaultValue = "") String category,
                         @RequestParam(required = false, defaultValue = "") String condition,
                         @RequestParam(required = false) BigDecimal minPrice,
                         @RequestParam(required = false) BigDecimal maxPrice,
                         @RequestParam(required = false, defaultValue = "newest") String sort,
                         Model model) {
        if (q != null && !q.isBlank()) {
            recentSearchBean.addSearch(q);
        }

        model.addAttribute("products",
                productService.browse(q, category, condition, minPrice, maxPrice, sort));
        model.addAttribute("q", q);
        model.addAttribute("category", category);
        model.addAttribute("condition", condition);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
        model.addAttribute("sort", sort);
        model.addAttribute("categories", productService.findDistinctCategories());
        model.addAttribute("conditions", productService.findDistinctConditions());
        model.addAttribute("recentSearches", recentSearchBean.getRecentSearches());
        model.addAttribute("wishlistProductIds", wishlistService.savedProductIdsForCurrentUser());
        model.addAttribute("recommendations", recommendationService.recommendForCurrentUser());
        return "browse";
    }
}
