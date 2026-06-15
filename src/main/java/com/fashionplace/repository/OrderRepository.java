package com.fashionplace.repository;

import com.fashionplace.model.Order;
import com.fashionplace.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for {@link Order} entities. Provides CRUD methods for free.
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * Returns a buyer's orders, newest first.
     *
     * @param buyer the purchasing user
     * @return the buyer's orders
     */
    List<Order> findByBuyerOrderByCreatedAtDesc(User buyer);
}
