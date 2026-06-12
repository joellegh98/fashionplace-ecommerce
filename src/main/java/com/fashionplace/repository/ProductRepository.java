package com.fashionplace.repository;

import com.fashionplace.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for {@link Product} entities (primary key type {@link Long}).
 *
 * <p>Extending {@link JpaRepository} provides CRUD methods (save, findById, findAll,
 * count, deleteById, ...) with no implementation code.</p>
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
}
