package com.fashionplace.service;

import com.fashionplace.model.User;
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

    /**
     * Disables a user account so it is treated as blocked. Admin accounts cannot be
     * disabled, to avoid locking the team out of the admin area.
     *
     * @param id the user to disable
     * @throws java.util.NoSuchElementException if no user has the given id
     * @throws IllegalStateException            if the user is an admin
     */
    public void disableUser(Long id) {
        User user = userRepository.findById(id).orElseThrow();
        if ("ADMIN".equals(user.getRole())) {
            throw new IllegalStateException("Admin accounts cannot be disabled.");
        }
        user.setDisabled(true);
        userRepository.save(user);
    }

    /**
     * Re-enables a previously disabled user account.
     *
     * @param id the user to enable
     * @throws java.util.NoSuchElementException if no user has the given id
     */
    public void enableUser(Long id) {
        User user = userRepository.findById(id).orElseThrow();
        user.setDisabled(false);
        userRepository.save(user);
    }
}
