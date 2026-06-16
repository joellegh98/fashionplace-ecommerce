package com.fashionplace.repository;

import com.fashionplace.model.Product;
import com.fashionplace.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for {@link Review} entities. Provides CRUD methods for free.
 */
@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    /**
     * Returns all reviews for a product, newest first.
     *
     * @param product the product to look up reviews for
     * @return reviews for that product
     */
    List<Review> findByProductOrderByCreatedAtDesc(Product product);

    /**
     * Removes all reviews for a product (required before deleting the product itself).
     *
     * @param product the product whose reviews to delete
     */
    void deleteByProduct(Product product);
}
