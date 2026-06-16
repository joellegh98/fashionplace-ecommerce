package com.fashionplace.repository;

import com.fashionplace.model.Product;
import com.fashionplace.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Collection;
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
    List<Product> findByTitleContainingIgnoreCaseAndDeletedFalseAndStatusNot(String title, String status);

    /**
     * Returns non-deleted products matching all supplied filters. A {@code null} or blank
     * parameter is ignored (no restriction on that field).
     */
    @Query("""
            SELECT p FROM Product p
            WHERE p.deleted = false
              AND p.status <> 'FLAGGED'
              AND (:keyword IS NULL OR LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')))
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

    @Query("SELECT DISTINCT p.category FROM Product p WHERE p.deleted = false AND p.category IS NOT NULL ORDER BY p.category")
    List<String> findDistinctCategories();

    @Query("SELECT DISTINCT p.condition FROM Product p WHERE p.deleted = false AND p.condition IS NOT NULL ORDER BY p.condition")
    List<String> findDistinctConditions();

    /**
     * Finds non-deleted products whose category is in the given set and whose status matches.
     * Used by the recommendation logic to suggest available items in categories the user likes.
     *
     * @param categories the categories to include
     * @param status     the required listing status (e.g. {@code ACTIVE})
     * @return matching products
     */
    List<Product> findByCategoryInAndStatusAndDeletedFalse(Collection<String> categories, String status);

    /**
     * Finds non-deleted products in one category with the given status (e.g. related products
     * on a detail page).
     *
     * @param category the category to match
     * @param status   the required listing status (e.g. {@code ACTIVE})
     * @return matching products
     */
    List<Product> findByCategoryAndStatusAndDeletedFalse(String category, String status);

    /**
     * Finds all non-deleted products listed by the given seller, newest first.
     *
     * @param seller the user who listed the products
     * @return that seller's products, ordered by id descending
     */
    List<Product> findBySellerAndDeletedFalseOrderByIdDesc(User seller);
}
