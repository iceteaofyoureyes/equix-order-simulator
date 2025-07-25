package com.equix.ordersimulator.domain.model.enums;

import com.equix.ordersimulator.application.exception.InvalidOrderSideException;
import com.fasterxml.jackson.annotation.JsonCreator;

public enum OrderSide {
    BUY, SELL;

    @JsonCreator
    public static OrderSide from(String value) {
        for (OrderSide s : OrderSide.values()) {
            if (s.name().equalsIgnoreCase(value)) {
                return s;
            }
        }
        throw new InvalidOrderSideException();
    }
}
