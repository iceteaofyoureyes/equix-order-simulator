package com.equix.ordersimulator.domain.repository;

import com.equix.ordersimulator.domain.model.Order;
import com.equix.ordersimulator.domain.model.enums.OrderStatus;

import java.util.List;
import java.util.Set;

public interface OrderRepository {

    Order createOrder(Order order);

    Order updateOrder(Order order);

    List<Order> getAllOrder();

    Order getOrderById(Long orderId);

    void addToBook(Order order);

    void removeFromBook(Order order);

    List<Order> getOrdersByStatus(OrderStatus orderStatus);

    List<Order> updateOrdersStatus(Set<Long> orderIds, OrderStatus orderStatus);
}
