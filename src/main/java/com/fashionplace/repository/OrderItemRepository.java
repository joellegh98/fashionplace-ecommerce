package com.fashionplace.repository;

import com.fashionplace.model.Order;
import com.fashionplace.model.OrderItem;
import com.fashionplace.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for {@link OrderItem} entities. Provides CRUD methods for free.
 */
@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    /**
     * Returns the line items belonging to an order.
     *
     * @param order the parent order
     * @return that order's line items
     */
    List<OrderItem> findByOrder(Order order);

    /**
     * Removes all order line items for a product (required before deleting the product itself).
     *
     * @param product the product whose order lines to delete
     */
    void deleteByProduct(Product product);
}
