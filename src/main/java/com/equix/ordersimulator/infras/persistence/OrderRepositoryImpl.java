package com.equix.ordersimulator.infras.persistence;

import com.equix.ordersimulator.domain.model.Order;
import com.equix.ordersimulator.domain.model.PriceLevel;
import com.equix.ordersimulator.domain.model.enums.OrderSide;
import com.equix.ordersimulator.domain.model.enums.OrderStatus;
import com.equix.ordersimulator.domain.repository.OrderRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class OrderRepositoryImpl implements OrderRepository {
    private final AtomicLong orderSequence = new AtomicLong(1L);
    private final Map<Long, Order> allOrders = new ConcurrentHashMap<>();
    private final Map<BigDecimal, PriceLevel> buyBook = new ConcurrentSkipListMap<>(Comparator.reverseOrder());
    private final Map<BigDecimal, PriceLevel> sellBook = new ConcurrentSkipListMap<>();

    @Override
    public Order createOrder(Order order) {
        order.setId(orderSequence.getAndIncrement());
        addToBook(order);
        allOrders.putIfAbsent(order.getId(), order);
        return new Order(order);
    }

    @Override
    public Order updateOrder(Order order) {
        Long orderId = order.getId();
        Order targetOrder = allOrders.get(orderId);

        if (Objects.isNull(targetOrder)) {
            return null;
        }

        allOrders.put(orderId, order);
        return new Order(order);
    }

    @Override
    public List<Order> getAllOrder() {
        return allOrders.values().stream().map(Order::new).toList();
    }

    @Override
    public Order getOrderById(Long orderId) {
        Order targetOrder = allOrders.get(orderId);

        if (Objects.isNull(targetOrder)) {
            return null;
        }

        return new Order(targetOrder);
    }

    @Override
    public void addToBook(Order order) {
        Map<BigDecimal, PriceLevel> book = OrderSide.BUY.equals(order.getSide()) ? buyBook : sellBook;
        PriceLevel level = book.computeIfAbsent(order.getPrice(), PriceLevel::new);
        level.add(order);
    }

    @Override
    public void removeFromBook(Order order) {
        Map<BigDecimal, PriceLevel> book = OrderSide.BUY.equals(order.getSide()) ? buyBook : sellBook;
        PriceLevel level = book.get(order.getPrice());
        if (level != null) {
            level.remove(order);
            if (level.isEmpty()) book.remove(order.getPrice());
        }
    }

    @Override
    public List<Order> getOrdersByStatus(OrderStatus orderStatus) {
        return allOrders.values().stream().filter(o -> orderStatus.equals(o.getStatus())).map(Order::new).toList();
    }

    @Override
    public List<Order> updateOrdersStatus(Set<Long> orderIds, OrderStatus orderStatus) {
        List<Order> targetOrder = allOrders.values().stream().filter(o -> orderIds.contains(o.getId())).toList();

        if (targetOrder.isEmpty()) {
            return Collections.emptyList();
        }

        targetOrder.forEach(o -> o.setStatus(orderStatus));
        return targetOrder;
    }
}
