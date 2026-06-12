package com.fashionplace.config;

import com.fashionplace.model.Product;
import com.fashionplace.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Inserts sample products at startup when the {@code product} table is empty.
 *
 * <p>Runs after the Spring context is ready ({@link CommandLineRunner}). Skips seeding
 * if any product already exists so restarts do not duplicate data.</p>
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private final ProductRepository productRepository;

    public DataSeeder(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public void run(String... args) {
        if (productRepository.count() > 0) {
            return;
        }

        productRepository.save(new Product(
                "Gold Hoop Earrings",
                "Classic 14k gold-plated hoop earrings, lightweight and perfect for everyday wear.",
                new BigDecimal("45.99"),
                "Jewelry", "New", "ACTIVE",
                "https://picsum.photos/seed/earrings/400/300"
        ));
        productRepository.save(new Product(
                "Silver Chain Necklace",
                "Delicate sterling silver chain with a minimalist pendant.",
                new BigDecimal("62.50"),
                "Jewelry", "New", "ACTIVE",
                "https://picsum.photos/seed/necklace/400/300"
        ));
        productRepository.save(new Product(
                "Leather Biker Jacket",
                "Genuine black leather jacket with zip front and quilted shoulders.",
                new BigDecimal("189.00"),
                "Clothing", "Used", "ACTIVE",
                "https://picsum.photos/seed/jacket/400/300"
        ));
        productRepository.save(new Product(
                "Vintage Denim Jeans",
                "High-waisted straight-leg jeans in a faded blue wash.",
                new BigDecimal("55.00"),
                "Clothing", "Used", "ACTIVE",
                "https://picsum.photos/seed/jeans/400/300"
        ));
        productRepository.save(new Product(
                "Pearl Bracelet",
                "Freshwater pearl bracelet with a silver clasp, elegant and timeless.",
                new BigDecimal("38.75"),
                "Jewelry", "New", "ACTIVE",
                "https://picsum.photos/seed/bracelet/400/300"
        ));
        productRepository.save(new Product(
                "Silk Evening Dress",
                "Floor-length emerald silk dress, ideal for formal occasions.",
                new BigDecimal("220.00"),
                "Clothing", "New", "SOLD",
                "https://picsum.photos/seed/dress/400/300"
        ));
    }
}
