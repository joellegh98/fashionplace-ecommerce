package com.fashionplace.service;

import com.fashionplace.model.Product;
import com.fashionplace.model.Review;
import com.fashionplace.model.User;
import com.fashionplace.repository.ReviewRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Business logic for product reviews.
 */
@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;

    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    /**
     * Returns all reviews for a product, newest first.
     *
     * @param product the product to look up
     * @return reviews for that product
     */
    public List<Review> findByProduct(Product product) {
        return reviewRepository.findByProductOrderByCreatedAtDesc(product);
    }

    /**
     * Computes the average star rating for a product, or {@code null} if there are no reviews.
     *
     * @param product the product to average
     * @return average rating (1–5), or {@code null} when empty
     */
    public Double averageRating(Product product) {
        List<Review> reviews = findByProduct(product);
        if (reviews.isEmpty()) {
            return null;
        }
        return reviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);
    }

    /**
     * Saves a new review for a product, attributed to the given user.
     *
     * @param product  the product being reviewed
     * @param reviewer the user submitting the review
     * @param rating   star rating (1–5)
     * @param comment  review text
     */
    public void addReview(Product product, User reviewer, int rating, String comment) {
        reviewRepository.save(new Review(rating, comment, reviewer, product));
    }
}
