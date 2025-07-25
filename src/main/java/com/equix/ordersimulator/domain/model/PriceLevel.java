package com.equix.ordersimulator.domain.model;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.LinkedHashMap;

@Data
public class PriceLevel implements Comparable<PriceLevel> {
    private final BigDecimal price;
    private final LinkedHashMap<Long, Order> orders = new LinkedHashMap<>();

    public PriceLevel(BigDecimal price) {
        this.price = price;
    }

    public void add(Order o) {
        orders.put(o.getId(), o);
    }

    public void remove(Order o) {
        orders.remove(o.getId());
    }

    public boolean isEmpty() {
        return orders.isEmpty();
    }

    public Collection<Order> getOrders() {
        return orders.values();
    }

    @Override
    public int compareTo(PriceLevel o) {
        return this.price.compareTo(o.getPrice());
    }
}
