package com.equix.ordersimulator.domain.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

@Data
@NoArgsConstructor
public class OrderBook {
    private String symbol;
    private Map<BigDecimal, PriceLevel> buyBook = new ConcurrentSkipListMap<>(Comparator.reverseOrder());;
    private Map<BigDecimal, PriceLevel> sellBook = new ConcurrentSkipListMap<>();

    public OrderBook(String symbol) {
        this.symbol = symbol;
    }

    public boolean isEmpty() {
        return buyBook.isEmpty() && sellBook.isEmpty();
    }
}
