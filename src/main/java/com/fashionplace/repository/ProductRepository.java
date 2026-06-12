package com.fashionplace.repository;

import com.fashionplace.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for {@link Product} entities (primary key type {@link Long}).
 *
 * <p>Extending {@link JpaRepository} provides CRUD methods (save, findById, findAll,
 * count, deleteById, ...) with no implementation code.</p>
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * Finds products whose title contains the given text, ignoring case. Spring Data
     * derives the SQL from the method name.
     *
     * @param title the keyword to search for within the product title
     * @return matching products
     */
    List<Product> findByTitleContainingIgnoreCase(String title);
}
