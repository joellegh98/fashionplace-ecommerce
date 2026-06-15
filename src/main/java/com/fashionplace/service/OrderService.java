package com.fashionplace.service;

import com.fashionplace.model.Order;
import com.fashionplace.model.OrderItem;
import com.fashionplace.model.Product;
import com.fashionplace.model.User;
import com.fashionplace.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Business logic for placing and reading orders.
 */
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductService productService;

    public OrderService(OrderRepository orderRepository, ProductService productService) {
        this.orderRepository = orderRepository;
        this.productService = productService;
    }

    /**
     * Creates an order and its line items atomically from a set of {@code productId → quantity}
     * pairs, reducing each product's stock by the purchased amount (and marking it {@code SOLD}
     * once its stock hits zero). Each line captures the product's current price. All writes
     * happen in one transaction, so if any step fails everything is rolled back.
     *
     * @param buyer           the purchasing user
     * @param items           cart contents as {@code productId → quantity}
     * @param shippingAddress where the order ships to
     * @return the persisted order (with generated id and saved line items)
     * @throws IllegalStateException if any product has insufficient stock
     */
    @Transactional
    public Order placeOrder(User buyer, Map<Long, Integer> items, String shippingAddress) {
        Order order = new Order(buyer, BigDecimal.ZERO, "PLACED", shippingAddress);

        BigDecimal total = BigDecimal.ZERO;
        for (Map.Entry<Long, Integer> entry : items.entrySet()) {
            Product product = productService.findById(entry.getKey());
            int quantity = entry.getValue();
            if (quantity > product.getQuantity()) {
                throw new IllegalStateException("Not enough stock for \"" + product.getTitle() + "\".");
            }
            BigDecimal unitPrice = product.getPrice();

            order.addItem(new OrderItem(product, quantity, unitPrice));
            total = total.add(unitPrice.multiply(BigDecimal.valueOf(quantity)));

            productService.reduceStock(product, quantity);
        }

        order.setTotalPrice(total);
        return orderRepository.save(order);
    }

    /**
     * Finds an order by id.
     *
     * @param id the order id
     * @return the order
     * @throws java.util.NoSuchElementException if no order has the given id
     */
    public Order findById(Long id) {
        return orderRepository.findById(id).orElseThrow();
    }
}
