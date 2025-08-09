package com.equix.ordersimulator.interfaces.mapper;

import com.equix.ordersimulator.domain.model.Order;
import com.equix.ordersimulator.interfaces.request.CreateOrderRequest;
import com.equix.ordersimulator.interfaces.response.OrderResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    default OrderResponse toOrderResponse(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .symbol(order.getSymbol())
                .price(order.getPrice())
                .quantity(order.getQuantity())
                .filledQuantity(order.getFilledQuantity())
                .side(order.getSide())
                .status(order.getStatus())
                .createdTime(order.getCreatedTime())
                .updatedTime(order.getUpdatedTime())
                .canceledTime(order.getCanceledTime())
                .build();
    }
    default Order toOrder(CreateOrderRequest request) {
        return Order.builder()
                .symbol(request.getSymbol())
                .price(request.getPrice())
                .quantity(request.getQuantity())
                .side(request.getSide())
                .build();
    }
}
