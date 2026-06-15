package com.fashionplace.service;

import com.fashionplace.model.Product;
import com.fashionplace.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

/**
 * Business-logic layer for products. Sits between the controllers and the
 * {@link ProductRepository}, so controllers depend on this service rather than on
 * the database access layer directly.
 */
@Service
public class ProductService {

    /** Maximum number of titles returned by the search autocomplete. */
    private static final int MAX_SUGGESTIONS = 10;

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * Returns every product.
     *
     * @return all products
     */
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    /**
     * Returns products matching the optional keyword and browse filters.
     *
     * @param keyword   optional title keyword
     * @param category  optional category (exact match)
     * @param condition optional item condition (exact match)
     * @param minPrice  optional minimum price (inclusive)
     * @param maxPrice  optional maximum price (inclusive)
     * @return products matching all non-empty criteria
     */
    public List<Product> browse(String keyword, String category, String condition,
                                BigDecimal minPrice, BigDecimal maxPrice, String sort) {
        List<Product> products = productRepository.filter(
                blankToNull(keyword),
                blankToNull(category),
                blankToNull(condition),
                minPrice,
                maxPrice
        );
        return applySort(products, sort);
    }

    /**
     * Sorts the given products. {@code newest} uses descending id as a stand-in until
     * {@code createdAt} is added to the entity.
     */
    private List<Product> applySort(List<Product> products, String sort) {
        String effectiveSort = (sort == null || sort.isBlank()) ? "newest" : sort;
        return switch (effectiveSort) {
            case "price_asc" -> products.stream()
                    .sorted(Comparator.comparing(Product::getPrice, Comparator.nullsLast(Comparator.naturalOrder())))
                    .toList();
            case "price_desc" -> products.stream()
                    .sorted(Comparator.comparing(Product::getPrice, Comparator.nullsLast(Comparator.reverseOrder())))
                    .toList();
            default -> products.stream()
                    .sorted(Comparator.comparing(Product::getId, Comparator.nullsLast(Comparator.reverseOrder())))
                    .toList();
        };
    }

    /**
     * Returns distinct category values present in the database (for filter dropdowns).
     */
    public List<String> findDistinctCategories() {
        return productRepository.findDistinctCategories();
    }

    /**
     * Returns distinct condition values present in the database (for filter dropdowns).
     */
    public List<String> findDistinctConditions() {
        return productRepository.findDistinctConditions();
    }

    private static String blankToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    /**
     * Returns up to {@value #MAX_SUGGESTIONS} product titles matching the keyword, for the
     * Browse search autocomplete. A blank or {@code null} keyword yields no suggestions.
     *
     * @param keyword the partial title typed by the user
     * @return matching product titles (at most {@value #MAX_SUGGESTIONS})
     */
    public List<String> suggestTitles(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return List.of();
        }
        return productRepository.findByTitleContainingIgnoreCase(keyword.trim()).stream()
                .map(Product::getTitle)
                .distinct()
                .limit(MAX_SUGGESTIONS)
                .toList();
    }

    /**
     * Finds a single product by its id.
     *
     * @param id the product id
     * @return the product
     * @throws java.util.NoSuchElementException if no product has the given id
     */
    public Product findById(Long id) {
        return productRepository.findById(id).orElseThrow();
    }

    /**
     * Reduces a product's stock by the purchased amount and persists it. When the remaining
     * quantity reaches zero the product is marked {@code SOLD}. Quantity never goes negative.
     *
     * @param product  the product to update
     * @param quantity the number of units sold
     * @return the saved product
     */
    public Product reduceStock(Product product, int quantity) {
        int remaining = Math.max(product.getQuantity() - quantity, 0);
        product.setQuantity(remaining);
        if (remaining == 0) {
            product.setStatus("SOLD");
        }
        return productRepository.save(product);
    }
}
