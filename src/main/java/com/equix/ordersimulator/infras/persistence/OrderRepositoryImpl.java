package com.equix.ordersimulator.infras.persistence;

import com.equix.ordersimulator.domain.model.Order;
import com.equix.ordersimulator.domain.model.OrderBook;
import com.equix.ordersimulator.domain.model.PriceLevel;
import com.equix.ordersimulator.domain.model.enums.OrderSide;
import com.equix.ordersimulator.domain.model.enums.OrderStatus;
import com.equix.ordersimulator.domain.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepository {
    private final AtomicLong orderSequence = new AtomicLong(1L);
    private final Map<Long, Order> allOrders = new ConcurrentHashMap<>();
    private final Map<String, OrderBook> orderBookMap = new HashMap<>();
    private final BlockingQueue<Order> orderQueue;

    @Override
    public Order createOrder(Order order) {
        order.setId(orderSequence.getAndIncrement());
        addToBook(order);
        allOrders.putIfAbsent(order.getId(), order);
        orderQueue.offer(order);
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
        OrderBook orderBook = orderBookMap.computeIfAbsent(order.getSymbol(), OrderBook::new);
        Map<BigDecimal, PriceLevel> book = OrderSide.BUY.equals(order.getSide()) ? orderBook.getBuyBook() : orderBook.getSellBook();
        PriceLevel level = book.computeIfAbsent(order.getPrice(), PriceLevel::new);
        level.add(order);
    }

    @Override
    public void removeFromBook(Order order) {
        OrderBook orderBook = orderBookMap.get(order.getSymbol());
        if (Objects.isNull(orderBook)) {
            return;
        }
        Map<BigDecimal, PriceLevel> book = order.getSide() == OrderSide.BUY ? orderBook.getBuyBook() : orderBook.getSellBook();

        PriceLevel level = book.get(order.getPrice());
        if (level != null) {
            level.remove(order);

            if (level.isEmpty()) {
                book.remove(order.getPrice());
            }
        }

        if (orderBook.isEmpty()) {
            orderBookMap.remove(orderBook.getSymbol());
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

    @Override
    public Optional<PriceLevel> getBestBidByOrderBook(String orderBookSymbol) {
        OrderBook orderBook = orderBookMap.get(orderBookSymbol);

        if (Objects.isNull(orderBook)) {
            return Optional.empty();
        }

        Map<BigDecimal, PriceLevel> buyBook = orderBook.getBuyBook();
        return buyBook.isEmpty() ? Optional.empty() : Optional.of(buyBook.entrySet().iterator().next().getValue());
    }

    @Override
    public Optional<PriceLevel> getBestAsk(String orderBookSymbol) {
        OrderBook orderBook = orderBookMap.get(orderBookSymbol);

        if (Objects.isNull(orderBook)) {
            return Optional.empty();
        }

        Map<BigDecimal, PriceLevel> sellBook = orderBook.getSellBook();
        return sellBook.isEmpty() ? Optional.empty() : Optional.of(sellBook.entrySet().iterator().next().getValue());
    }

    @Override
    public void removePriceLevel(String orderBookSymbol, OrderSide side, BigDecimal price) {
        OrderBook orderBook = orderBookMap.get(orderBookSymbol);
        if (side == OrderSide.BUY) {
            orderBook.getBuyBook().remove(price);
        } else {
            orderBook.getSellBook().remove(price);
        }
    }

}
