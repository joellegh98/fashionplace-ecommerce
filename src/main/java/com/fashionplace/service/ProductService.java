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
