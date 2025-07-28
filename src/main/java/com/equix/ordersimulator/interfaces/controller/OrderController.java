package com.equix.ordersimulator.interfaces.controller;

import com.equix.ordersimulator.application.service.OrderService;
import com.equix.ordersimulator.domain.model.Order;
import com.equix.ordersimulator.interfaces.constant.HttpStatusCode;
import com.equix.ordersimulator.interfaces.mapper.OrderMapper;
import com.equix.ordersimulator.interfaces.request.CreateOrderRequest;
import com.equix.ordersimulator.interfaces.response.BaseResponse;
import com.equix.ordersimulator.interfaces.response.OrderResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final OrderMapper orderMapper;

    @PostMapping
    @Operation(summary = "Create a new Order")
    @ApiResponses(value = {
            @ApiResponse(responseCode = HttpStatusCode.STATUS_CREATED, description = "Create new Order successfully"),
            @ApiResponse(responseCode = HttpStatusCode.STATUS_NOT_FOUND, description = "Return on invalid Symbol (Symbol that does not exist)"),
            @ApiResponse(responseCode = HttpStatusCode.STATUS_BAD_REQUEST, description = "Return on invalid payload (Detailed errors will be included)"),
            @ApiResponse(responseCode = HttpStatusCode.STATUS_FORBIDDEN, description = "Forbidden: User does not have required permission")}
    )
    @PreAuthorize("hasAuthority('ORDER.CREATE')")
    public ResponseEntity<BaseResponse<OrderResponse>> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        Order createdOrder = orderService.createOrder(request);
        return ResponseEntity.
                status(HttpStatusCode.CREATED.value).
                body(BaseResponse.<OrderResponse>builder().
                        code(HttpStatusCode.CREATED.value).
                        data(orderMapper.toOrderResponse(createdOrder)).
                        build()
                );

    }

    @PostMapping(path = "{id:[1-9][0-9]*}/cancel")
    @Operation(summary = "Cancel a PENDING Order by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = HttpStatusCode.STATUS_OK, description = "Found Order with specified id"),
            @ApiResponse(responseCode = HttpStatusCode.STATUS_NOT_FOUND, description = "Invalid Order id format supplied/Did not find Order with specified id"),
            @ApiResponse(responseCode = HttpStatusCode.STATUS_BAD_REQUEST, description = "Invalid Cancel status"),
            @ApiResponse(responseCode = HttpStatusCode.STATUS_FORBIDDEN, description = "Forbidden: User does not have required permission")}
    )
    @PreAuthorize("hasAuthority('ORDER.CANCEL')")
    public ResponseEntity<BaseResponse<OrderResponse>> cancelOrder(@PathVariable Long id) {
        Order canceledOrder = orderService.cancelOrder(id);
        return ResponseEntity.
                status(HttpStatusCode.OK.value).
                body(BaseResponse.<OrderResponse>builder().
                        code(HttpStatusCode.OK.value).
                        data(orderMapper.toOrderResponse(canceledOrder)).
                        build()
                );

    }

    @GetMapping(path = "{id:[1-9][0-9]*}")
    @Operation(summary = "Get an Order by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = HttpStatusCode.STATUS_OK, description = "Found Order with specified id"),
            @ApiResponse(responseCode = HttpStatusCode.STATUS_NOT_FOUND, description = "Invalid Order id format supplied/Did not find Order with specified id"),
            @ApiResponse(responseCode = HttpStatusCode.STATUS_FORBIDDEN, description = "Forbidden: User does not have required permission")}
    )
    @PreAuthorize("hasAuthority('ORDER.GET')")
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

    @GetMapping
    @Operation(summary = "Get all Orders")
    @ApiResponses(value = {
            @ApiResponse(responseCode = HttpStatusCode.STATUS_OK, description = "Returns all Orders that created"),
            @ApiResponse(responseCode = HttpStatusCode.STATUS_FORBIDDEN, description = "Forbidden: User does not have required permission")}
    )
    @PreAuthorize("hasAuthority('ORDER.GET_ALL')")
    public ResponseEntity<BaseResponse<List<OrderResponse>>> getAllOrders() {
        List<Order> orders = orderService.getAllOrder();
        List<OrderResponse> orderResponses;

        if (orders.isEmpty()) {
            orderResponses = Collections.emptyList();
        } else {
            orderResponses = orders.stream().map(orderMapper::toOrderResponse).toList();
        }

        return ResponseEntity.
                status(HttpStatusCode.OK.value).
                body(BaseResponse.<List<OrderResponse>>builder().
                        code(HttpStatusCode.OK.value).
                        data(orderResponses).
                        build()
                );
    }

}
