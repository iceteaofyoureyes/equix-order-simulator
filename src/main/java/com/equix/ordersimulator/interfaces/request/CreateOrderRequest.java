package com.equix.ordersimulator.interfaces.request;

import com.equix.ordersimulator.domain.model.enums.OrderSide;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateOrderRequest {
    @NotBlank(message = "Symbol is required and not empty")
    private String symbol;
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    @Digits(integer = 20, fraction = 10, message = "Price format invalid")
    private BigDecimal price;
    @Min(value = 1, message = "Quantity must be greater than 0")
    private Integer quantity;
    @NotNull(message = "Side is required")
    private OrderSide side;
}
