package com.fashionplace.config;

import com.fashionplace.model.Product;
import com.fashionplace.model.ProductCategories;
import com.fashionplace.model.Review;
import com.fashionplace.model.User;
import com.fashionplace.repository.ProductRepository;
import com.fashionplace.repository.ReviewRepository;
import com.fashionplace.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * Seeds sample users and products at startup, each only if its table is empty
 * (so restarts never duplicate data).
 */
@Component
@Order(1)
public class DataSeeder implements CommandLineRunner {

    /** Plain-text dev password for every seeded account (hashed before persist). */
    private static final String SEED_PASSWORD = "password";

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(ProductRepository productRepository,
                      UserRepository userRepository,
                      ReviewRepository reviewRepository,
                      PasswordEncoder passwordEncoder) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.reviewRepository = reviewRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        seedUsers();
        seedProducts();
        seedReviews();
        migrateLegacyCategories();
    }

    /** Inserts sample regular users with BCrypt-hashed passwords (admin is created by {@link AdminInitializer}). */
    private void seedUsers() {
        if (userRepository.count() > 0) {
            return;
        }

        String hashedPassword = passwordEncoder.encode(SEED_PASSWORD);

        userRepository.save(new User(
                "alice",
                "alice@example.com",
                hashedPassword,
                "ADMIN",
                "42 Maple Avenue, Rivertown"
        ));
        userRepository.save(new User(
                "bob",
                "bob@example.com",
                hashedPassword,
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
                "Earrings", "New", "ACTIVE", 5,
                "https://www.longsjewelers.com/cdn/shop/products/GEH2588A.jpg?v=1715249533"
        ), alice);
        save(new Product(
                "Silver Chain Necklace",
                "Delicate sterling silver chain with a minimalist pendant.",
                new BigDecimal("62.50"),
                "Necklace", "New", "ACTIVE", 3,
                "https://prya.co.uk/cdn/shop/products/IMG_92992-PRYA-Necklaces.jpg?v=1619772139"
        ), alice);
        save(new Product(
                "Leather Biker Jacket",
                "Genuine black leather jacket with zip front and quilted shoulders.",
                new BigDecimal("189.00"),
                "Shirts", "Used", "ACTIVE", 1,
                "https://cdn-images.farfetch-contents.com/17/81/19/24/17811924_37690679_600.jpg"
        ), bob);
        save(new Product(
                "Vintage Denim Jeans",
                "High-waisted straight-leg jeans in a faded blue wash.",
                new BigDecimal("55.00"),
                "Jeans", "Used", "ACTIVE", 4,
                "https://www.bigw.com.au/medias/sys_master/images/images/h85/ha0/116332415582238.jpg"
        ), bob);
        save(new Product(
                "Pearl Bracelet",
                "Freshwater pearl bracelet with a silver clasp, elegant and timeless.",
                new BigDecimal("38.75"),
                "Bracelet", "New", "ACTIVE", 2,
                "https://www.jerseypearl.com/wp-content/uploads/2023/07/Zara-Freshwater-Pearl-Multi-Natural-Bracelet.jpg"
        ), alice);
        save(new Product(
                "Silk Evening Dress",
                "Floor-length emerald silk dress, ideal for formal occasions.",
                new BigDecimal("220.00"),
                "Bottoms", "New", "SOLD", 0,
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTXa7eixb7OwHn5k8EXd7pUGDlSBk-do_q0drqZG6QfmMw-FXohQmFVNnN1&s=10"
        ), bob);
    }

    /** Attaches the seller to the product and persists it. */
    private void save(Product product, User seller) {
        product.setSeller(seller);
        productRepository.save(product);
    }

    /** Maps old broad categories (Jewelry / Clothing) to the new category list. */
    private void migrateLegacyCategories() {
        for (Product product : productRepository.findAll()) {
            String category = product.getCategory();
            if (category == null || ProductCategories.ALL.contains(category)) {
                continue;
            }
            String title = product.getTitle() == null ? "" : product.getTitle().toLowerCase();
            if ("Jewelry".equals(category)) {
                if (title.contains("earring")) {
                    product.setCategory("Earrings");
                } else if (title.contains("necklace")) {
                    product.setCategory("Necklace");
                } else if (title.contains("bracelet")) {
                    product.setCategory("Bracelet");
                } else if (title.contains("ring")) {
                    product.setCategory("Ring");
                } else if (title.contains("glasses")) {
                    product.setCategory("Glasses");
                } else {
                    product.setCategory("Earrings");
                }
            } else if ("Clothing".equals(category)) {
                if (title.contains("jean")) {
                    product.setCategory("Jeans");
                } else if (title.contains("shoe")) {
                    product.setCategory("Shoes");
                } else if (title.contains("underwear")) {
                    product.setCategory("Underwear");
                } else if (title.contains("dress") || title.contains("skirt")) {
                    product.setCategory("Bottoms");
                } else if (title.contains("shirt") || title.contains("jacket")) {
                    product.setCategory("Shirts");
                } else {
                    product.setCategory("Shirts");
                }
            }
            productRepository.save(product);
        }
    }

    /** Inserts sample reviews when the {@code review} table is empty. */
    private void seedReviews() {
        if (reviewRepository.count() > 0) {
            return;
        }

        User bob = userRepository.findByUsername("bob").orElse(null);
        User alice = userRepository.findByUsername("alice").orElse(null);
        Product earrings = productRepository.findAll().stream()
                .filter(p -> "Gold Hoop Earrings".equals(p.getTitle()))
                .findFirst()
                .orElse(null);

        if (earrings == null || bob == null || alice == null) {
            return;
        }

        reviewRepository.save(new Review(
                5,
                "Beautiful earrings, exactly as described!",
                bob,
                earrings
        ));
        reviewRepository.save(new Review(
                4,
                "Lovely quality, slightly smaller than I expected.",
                alice,
                earrings
        ));
    }
}
