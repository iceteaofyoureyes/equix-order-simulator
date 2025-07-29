package com.equix.ordersimulator.domain.model;

import com.equix.ordersimulator.application.exception.AppException;
import com.equix.ordersimulator.application.exception.ErrorCode;
import com.equix.ordersimulator.domain.model.enums.OrderSide;
import com.equix.ordersimulator.domain.model.enums.OrderStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

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
    private Integer filledQuantity;
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
        this.filledQuantity = source.getFilledQuantity();
        this.side = source.getSide();
        this.status = source.getStatus();
        this.createdTime = source.getCreatedTime();
        this.updatedTime = source.getUpdatedTime();
        this.canceledTime = source.getCanceledTime();
        this.userId = source.getUserId();
    }

    public void fill(Integer qty) {
        if (qty <= 0) {
            throw new AppException(ErrorCode.BUSINESS_RULE_EXCEPTION, "Fill quantity must be positive");
        }
        if (this.filledQuantity + qty > this.quantity) {
            throw new AppException(ErrorCode.BUSINESS_RULE_EXCEPTION, "Fill quantity exceeds order originalQuantity!");
        }
        this.filledQuantity += qty;
    }

    public boolean isFilled() {
        return Objects.equals(this.filledQuantity, this.quantity);
    }

    public Integer getRemainingQuantity() {
        return quantity - filledQuantity;
    }
}
