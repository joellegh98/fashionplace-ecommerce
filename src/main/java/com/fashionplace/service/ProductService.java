package com.fashionplace.service;

import com.fashionplace.model.Product;
import com.fashionplace.repository.ProductRepository;
import org.springframework.stereotype.Service;

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
     * Returns products whose title matches the keyword. A blank or {@code null}
     * keyword returns all products.
     *
     * @param keyword the search text (may be blank or {@code null})
     * @return matching products, or all products when no keyword is given
     */
    public List<Product> search(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return productRepository.findAll();
        }
        return productRepository.findByTitleContainingIgnoreCase(keyword.trim());
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
}
