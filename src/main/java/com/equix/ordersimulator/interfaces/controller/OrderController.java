package com.equix.ordersimulator.interfaces.controller;

import com.equix.ordersimulator.application.service.OrderService;
import com.equix.ordersimulator.domain.model.Order;
import com.equix.ordersimulator.interfaces.config.HttpStatusCode;
import com.equix.ordersimulator.interfaces.mapper.OrderMapper;
import com.equix.ordersimulator.interfaces.request.CreateOrderRequest;
import com.equix.ordersimulator.interfaces.response.BaseResponse;
import com.equix.ordersimulator.interfaces.response.OrderResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @GetMapping(path = "{id:[1-9][0-9]*}")
    @Operation(summary = "Get an Order by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = HttpStatusCode.STATUS_OK, description = "Found Order with specified id"),
            @ApiResponse(responseCode = HttpStatusCode.STATUS_NOT_FOUND, description = "Invalid Order id format supplied/Did not find Order with specified id")}
    )
    public ResponseEntity<BaseResponse<OrderResponse>> getOrderById(@PathVariable Long id) {
        Order targetOrder = orderService.getOrderById(id);
        return ResponseEntity.
                status(HttpStatusCode.OK.value).
                body(BaseResponse.<OrderResponse>builder().
                        code(HttpStatusCode.OK.value).
                        data(orderMapper.toOrderResponse(targetOrder)).
                        build()
                );
    }


}
