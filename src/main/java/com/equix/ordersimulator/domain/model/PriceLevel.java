package com.equix.ordersimulator.domain.model;

import lombok.Data;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.Queue;

@Data
public class PriceLevel implements Comparable<PriceLevel> {
    @Getter
    private final BigDecimal price;
    private final Queue<Order> orders = new LinkedList<>();

    public void add(Order order) {
        orders.add(order);
    }

    public Order peek() {
        return orders.peek();
    }

    public Order poll() {
        return orders.poll();
    }

    public boolean isEmpty() {
        return orders.isEmpty();
    }

    public void remove(Order order) {
        orders.remove(order);
    }

    @Override
    public int compareTo(PriceLevel o) {
        return this.price.compareTo(o.getPrice());
    }
}
