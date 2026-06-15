package com.fashionplace.controller;

import com.fashionplace.service.CurrentUserProvider;
import com.fashionplace.service.OrderService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Shows the current (fake) user's orders.
 */
@Controller
public class OrderController {

    private final OrderService orderService;
    private final CurrentUserProvider currentUserProvider;

    public OrderController(OrderService orderService, CurrentUserProvider currentUserProvider) {
        this.orderService = orderService;
        this.currentUserProvider = currentUserProvider;
    }

    /**
     * Lists the current user's orders, newest first.
     *
     * @param model holds {@code orders} for the view
     * @return the {@code orders} view name
     */
    @GetMapping("/orders")
    public String orders(Model model) {
        model.addAttribute("orders",
                orderService.findByBuyer(currentUserProvider.getCurrentUser()));
        return "orders";
    }
}
