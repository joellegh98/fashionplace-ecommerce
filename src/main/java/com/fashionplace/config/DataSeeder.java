package com.fashionplace.config;

import com.fashionplace.model.Product;
import com.fashionplace.model.User;
import com.fashionplace.repository.ProductRepository;
import com.fashionplace.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Seeds sample users and products at startup, each only if its table is empty
 * (so restarts never duplicate data).
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public DataSeeder(ProductRepository productRepository, UserRepository userRepository) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) {
        seedUsers();
        seedProducts();
    }

    /** Inserts one admin and two regular users (placeholder passwords until Phase 9). */
    private void seedUsers() {
        if (userRepository.count() > 0) {
            return;
        }

        userRepository.save(new User(
                "admin",
                "admin@fashionplace.com",
                "placeholder-hash",
                "ADMIN",
                "1 Market Street, Springfield"
        ));
        userRepository.save(new User(
                "alice",
                "alice@example.com",
                "placeholder-hash",
                "USER",
                "42 Maple Avenue, Rivertown"
        ));
        userRepository.save(new User(
                "bob",
                "bob@example.com",
                "placeholder-hash",
                "USER",
                "7 Oak Lane, Hillside"
        ));
    }

    /**
     * Inserts sample products when the {@code product} table is empty, each attributed
     * to one of the seeded users as its seller.
     */
    private void seedProducts() {
        if (productRepository.count() > 0) {
            return;
        }

        User alice = userRepository.findByUsername("alice").orElse(null);
        User bob = userRepository.findByUsername("bob").orElse(null);

        save(new Product(
                "Gold Hoop Earrings",
                "Classic 14k gold-plated hoop earrings, lightweight and perfect for everyday wear.",
                new BigDecimal("45.99"),
                "Jewelry", "New", "ACTIVE",
                "https://picsum.photos/seed/earrings/400/300"
        ), alice);
        save(new Product(
                "Silver Chain Necklace",
                "Delicate sterling silver chain with a minimalist pendant.",
                new BigDecimal("62.50"),
                "Jewelry", "New", "ACTIVE",
                "https://picsum.photos/seed/necklace/400/300"
        ), alice);
        save(new Product(
                "Leather Biker Jacket",
                "Genuine black leather jacket with zip front and quilted shoulders.",
                new BigDecimal("189.00"),
                "Clothing", "Used", "ACTIVE",
                "https://picsum.photos/seed/jacket/400/300"
        ), bob);
        save(new Product(
                "Vintage Denim Jeans",
                "High-waisted straight-leg jeans in a faded blue wash.",
                new BigDecimal("55.00"),
                "Clothing", "Used", "ACTIVE",
                "https://picsum.photos/seed/jeans/400/300"
        ), bob);
        save(new Product(
                "Pearl Bracelet",
                "Freshwater pearl bracelet with a silver clasp, elegant and timeless.",
                new BigDecimal("38.75"),
                "Jewelry", "New", "ACTIVE",
                "https://picsum.photos/seed/bracelet/400/300"
        ), alice);
        save(new Product(
                "Silk Evening Dress",
                "Floor-length emerald silk dress, ideal for formal occasions.",
                new BigDecimal("220.00"),
                "Clothing", "New", "SOLD",
                "https://picsum.photos/seed/dress/400/300"
        ), bob);
    }

    /** Attaches the seller to the product and persists it. */
    private void save(Product product, User seller) {
        product.setSeller(seller);
        productRepository.save(product);
    }
}
