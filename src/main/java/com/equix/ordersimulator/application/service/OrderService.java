package com.equix.ordersimulator.application.service;

import com.equix.ordersimulator.application.dto.SimulateExecutionResult;
import com.equix.ordersimulator.application.dto.constant.OrderAction;
import com.equix.ordersimulator.application.exception.AppException;
import com.equix.ordersimulator.application.exception.ErrorCode;
import com.equix.ordersimulator.application.exception.ResourceNotFoundException;
import com.equix.ordersimulator.domain.model.Order;
import com.equix.ordersimulator.domain.model.enums.OrderStatus;
import com.equix.ordersimulator.domain.repository.OrderRepository;
import com.equix.ordersimulator.domain.repository.SymbolRepository;
import com.equix.ordersimulator.interfaces.mapper.OrderMapper;
import com.equix.ordersimulator.interfaces.request.CreateOrderRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final SymbolRepository symbolRepository;
    private final OrderMapper orderMapper;
    private final LoggingService loggingService;

    public Order createOrder(CreateOrderRequest request) {
        Set<String> allSymbols = symbolRepository.getAllSymbols();
        String symbol = request.getSymbol();

        if (!allSymbols.contains(symbol)) {
            String errorMessage = String.format("Symbol '%s' is not exists", symbol);
            loggingService.logOrderInfo(OrderAction.CREATE_ORDER, null, "Create new Order failed due to " + errorMessage);
            throw new ResourceNotFoundException(errorMessage);
        }

        LocalDateTime currentTime = LocalDateTime.now();

        Order order = orderMapper.toOrder(request);
        order.setFilledQuantity(0);
        order.setStatus(OrderStatus.PENDING);
        order.setCreatedTime(currentTime);
        order.setUpdatedTime(currentTime);
        //todo get userId from spring security context holder and set to order.userId
        Order createdOrder = orderRepository.createOrder(order);
        loggingService.logCreateOrder(createdOrder, true, "Created a new Order.");
        return createdOrder;
    }

    public Order cancelOrder(Long orderId) {
        Order targetOrder = orderRepository.getOrderById(orderId);

        if (Objects.isNull(targetOrder)) {
            String errorMessage = String.format("Order with id %d not found", orderId);
            loggingService.logOrderInfo(OrderAction.CANCEL_ORDER, orderId, errorMessage);
            throw new ResourceNotFoundException(errorMessage);
        }

        if (!OrderStatus.PENDING.equals(targetOrder.getStatus())) {
            loggingService.logCancelOrder(targetOrder, false, "Cancel order failed. Only PENDING orders can be canceled");
            throw new AppException(ErrorCode.BUSINESS_RULE_EXCEPTION, "Invalid order Status. Only PENDING orders can be canceled");
        }

        targetOrder.setStatus(OrderStatus.CANCELLED);
        targetOrder.setCanceledTime(LocalDateTime.now());
        //todo xóa khỏi queue chờ match order

        Order canceledOrder = orderRepository.updateOrder(targetOrder);
        loggingService.logCancelOrder(canceledOrder, true, "Cancel order Successfully");
        return canceledOrder;
    }

    public List<Order> getAllOrder() {
        return orderRepository.getAllOrder();
    }

    public Order getOrderById(Long orderId) {
        Order targetOrder = orderRepository.getOrderById(orderId);

        if (Objects.isNull(targetOrder)) {
            throw new ResourceNotFoundException(String.format("Order with id %d not found", orderId));
        }

        return targetOrder;
    }

    public SimulateExecutionResult simulateExecution() {
        List<Order> pendingOrders = orderRepository.getOrdersByStatus(OrderStatus.PENDING);

        if (pendingOrders.isEmpty()) {
            String errorMessage = String.format("Simulate execute Order failed. No %s Order", OrderStatus.PENDING);
            loggingService.logOrderInfo(OrderAction.EXECUTE_ORDER, null, errorMessage);
            throw new AppException(ErrorCode.BUSINESS_RULE_EXCEPTION, errorMessage);
        }

        Set<Long> executeOrderIds = new HashSet<>();

        for (Order o: pendingOrders) {
            if (RandomUtils.insecure().randomBoolean()) {
                executeOrderIds.add(o.getId());
            }
        }

        List<Order> executedOrders = orderRepository.updateOrdersStatus(executeOrderIds, OrderStatus.EXECUTED);
        loggingService.logOrderInfo(OrderAction.EXECUTE_ORDER, null, String.format("Simulate execute Order successfully. Updated %d PENDING Order to EXECUTED status", executedOrders.size()));
        return new SimulateExecutionResult(executedOrders.size(), executeOrderIds);
    }
}
