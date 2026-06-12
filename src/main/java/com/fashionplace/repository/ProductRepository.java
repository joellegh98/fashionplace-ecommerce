package com.fashionplace.repository;

import com.fashionplace.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
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

    /**
     * Returns products matching all supplied filters. A {@code null} or blank parameter
     * is ignored (no restriction on that field).
     */
    @Query("""
            SELECT p FROM Product p
            WHERE (:keyword IS NULL OR LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')))
              AND (:category IS NULL OR p.category = :category)
              AND (:condition IS NULL OR p.condition = :condition)
              AND (:minPrice IS NULL OR p.price >= :minPrice)
              AND (:maxPrice IS NULL OR p.price <= :maxPrice)
            """)
    List<Product> filter(@Param("keyword") String keyword,
                         @Param("category") String category,
                         @Param("condition") String condition,
                         @Param("minPrice") BigDecimal minPrice,
                         @Param("maxPrice") BigDecimal maxPrice);

    @Query("SELECT DISTINCT p.category FROM Product p WHERE p.category IS NOT NULL ORDER BY p.category")
    List<String> findDistinctCategories();

    @Query("SELECT DISTINCT p.condition FROM Product p WHERE p.condition IS NOT NULL ORDER BY p.condition")
    List<String> findDistinctConditions();
}
