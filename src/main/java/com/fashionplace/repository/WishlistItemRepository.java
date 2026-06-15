package com.fashionplace.repository;

import com.fashionplace.model.Product;
import com.fashionplace.model.User;
import com.fashionplace.model.WishlistItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for {@link WishlistItem} entities. Provides CRUD methods for free.
 */
@Repository
public interface WishlistItemRepository extends JpaRepository<WishlistItem, Long> {

    /**
     * Returns all items saved by the given user.
     *
     * @param owner the wishlist owner
     * @return the user's wishlist items
     */
    List<WishlistItem> findByOwner(User owner);

    /**
     * Checks whether the user has already saved the given product.
     *
     * @param owner   the wishlist owner
     * @param product the product to test
     * @return {@code true} if the product is already on the user's wishlist
     */
    boolean existsByOwnerAndProduct(User owner, Product product);

    /**
     * Removes the given user's saved entry for a product, if present.
     *
     * @param owner   the wishlist owner
     * @param product the product to unsave
     */
    void deleteByOwnerAndProduct(User owner, Product product);
}
