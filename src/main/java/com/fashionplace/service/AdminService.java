package com.fashionplace.service;

import com.fashionplace.repository.OrderRepository;
import com.fashionplace.repository.ProductRepository;
import com.fashionplace.repository.UserRepository;
import com.fashionplace.web.AdminDashboard;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

/**
 * Aggregates data from the user, product and order repositories for the admin area,
 * and delegates product moderation actions to {@link ProductService}.
 */
@Service
public class AdminService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final ProductService productService;

    public AdminService(UserRepository userRepository,
                        ProductRepository productRepository,
                        OrderRepository orderRepository,
                        ProductService productService) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.productService = productService;
    }

    /**
     * Builds a dashboard snapshot: total counts plus every user, product and order
     * (newest first where an id ordering applies).
     *
     * @return the populated {@link AdminDashboard}
     */
    public AdminDashboard loadDashboard() {
        Sort newestFirst = Sort.by(Sort.Direction.DESC, "id");
        return new AdminDashboard(
                userRepository.count(),
                productRepository.count(),
                orderRepository.count(),
                userRepository.findAll(newestFirst),
                productRepository.findAll(newestFirst),
                orderRepository.findAll(newestFirst));
    }

    /** Soft-deletes any product, regardless of who listed it. */
    public void deleteProduct(Long id) {
        productService.deleteListing(id);
    }

    /** Flags a product so it is hidden from the public catalogue. */
    public void flagProduct(Long id) {
        productService.flagListing(id);
    }

    /** Clears an admin flag and restores the listing status. */
    public void unflagProduct(Long id) {
        productService.unflagListing(id);
    }
}
