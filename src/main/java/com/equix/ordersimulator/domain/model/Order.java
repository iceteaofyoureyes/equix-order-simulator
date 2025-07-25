package com.equix.ordersimulator.domain.model;

import com.equix.ordersimulator.domain.model.enums.OrderSide;
import com.equix.ordersimulator.domain.model.enums.OrderStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    private Long id;
    private String symbol;
    private BigDecimal price;
    private Integer quantity;
    private OrderSide side;
    private OrderStatus status;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
    private LocalDateTime canceledTime;
    private Long userId;

    public Order (Order source) {
        this.id = source.getId();
        this.symbol = source.getSymbol();
        this.price = source.getPrice();
        this.quantity = source.getQuantity();
        this.side = source.getSide();
        this.status = source.getStatus();
        this.createdTime = source.getCreatedTime();
        this.updatedTime = source.getUpdatedTime();
        this.canceledTime = source.getCanceledTime();
        this.userId = source.getUserId();
    }
}
