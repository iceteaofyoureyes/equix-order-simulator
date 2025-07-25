package com.equix.ordersimulator.interfaces.controller;

import com.equix.ordersimulator.application.service.OrderService;
import com.equix.ordersimulator.domain.model.Order;
import com.equix.ordersimulator.interfaces.mapper.OrderMapper;
import com.equix.ordersimulator.interfaces.request.CreateOrderRequest;
import com.equix.ordersimulator.interfaces.response.BaseResponse;
import com.equix.ordersimulator.interfaces.response.OrderResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final OrderMapper orderMapper;

    @PostMapping
    public ResponseEntity<BaseResponse<OrderResponse>> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        Order createdOrder = orderService.createOrder(request);
        return ResponseEntity.
                status(HttpStatus.CREATED).
                body(BaseResponse.<OrderResponse>builder().
                        code(HttpStatus.CREATED.value()).
                        data(orderMapper.toOrderResponse(createdOrder)).
                        build()
                );

    }

    @PostMapping(path = "{id}/cancel")
    public ResponseEntity<BaseResponse<OrderResponse>> createOrder(@PathVariable @Valid @Min(1) Long id) {
        Order canceledOrder = orderService.cancelOrder(id);
        return ResponseEntity.
                status(HttpStatus.OK).
                body(BaseResponse.<OrderResponse>builder().
                        code(HttpStatus.OK.value()).
                        data(orderMapper.toOrderResponse(canceledOrder)).
                        build()
                );

    }


}
