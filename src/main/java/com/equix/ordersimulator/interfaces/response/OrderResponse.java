package com.equix.ordersimulator.interfaces.response;

import com.equix.ordersimulator.domain.model.enums.OrderSide;
import com.equix.ordersimulator.domain.model.enums.OrderStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponse {
    private Long id;
    private String symbol;
    private BigDecimal price;
    private Integer quantity;
    private OrderSide side;
    private OrderStatus status;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
    private LocalDateTime canceledTime;
}
