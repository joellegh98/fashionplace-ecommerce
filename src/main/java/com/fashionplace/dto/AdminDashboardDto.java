package com.fashionplace.dto;

import com.fashionplace.model.ActivityLog;
import com.fashionplace.model.Order;
import com.fashionplace.model.Product;
import com.fashionplace.model.User;

import java.util.List;

/**
 * Read-only snapshot for the admin dashboard: total counts plus recent rows
 * for the users, products and orders tables, and the latest activity log entries.
 */
public class AdminDashboardDto {

    private final long userCount;
    private final long productCount;
    private final long orderCount;
    private final List<User> users;
    private final List<Product> products;
    private final List<Order> orders;
    private final List<ActivityLog> activity;

    public AdminDashboardDto(long userCount, long productCount, long orderCount,
                             List<User> users, List<Product> products, List<Order> orders,
                             List<ActivityLog> activity) {
        this.userCount = userCount;
        this.productCount = productCount;
        this.orderCount = orderCount;
        this.users = users;
        this.products = products;
        this.orders = orders;
        this.activity = activity;
    }

    /** @return total number of registered users */
    public long getUserCount() {
        return userCount;
    }

    /** @return total number of products (including soft-deleted) */
    public long getProductCount() {
        return productCount;
    }

    /** @return total number of placed orders */
    public long getOrderCount() {
        return orderCount;
    }

    /** @return the users shown in the dashboard table */
    public List<User> getUsers() {
        return users;
    }

    /** @return the products shown in the dashboard table */
    public List<Product> getProducts() {
        return products;
    }

    /** @return the orders shown in the dashboard table */
    public List<Order> getOrders() {
        return orders;
    }

    /** @return the recent activity log entries shown in the dashboard */
    public List<ActivityLog> getActivity() {
        return activity;
    }
}
