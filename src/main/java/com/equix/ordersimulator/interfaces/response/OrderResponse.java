package com.equix.ordersimulator.interfaces.response;

import com.equix.ordersimulator.domain.model.enums.OrderSide;
import com.equix.ordersimulator.domain.model.enums.OrderStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
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
    private Integer filledQuantity;
    private OrderSide side;
    private OrderStatus status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private LocalDateTime createdTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private LocalDateTime updatedTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private LocalDateTime canceledTime;
}
