package com.equix.ordersimulator.domain.repository;

import com.equix.ordersimulator.domain.model.Order;

import java.util.List;

public interface OrderRepository {

    Order createOrder(Order order);

    Order updateOrder(Order order);

    List<Order> getAllOrder();

    Order getOrderById(Long orderId);

    void addToBook(Order order);

    void removeFromBook(Order order);

}
