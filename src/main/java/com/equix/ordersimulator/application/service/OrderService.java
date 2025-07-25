package com.equix.ordersimulator.application.service;

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
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final SymbolRepository symbolRepository;
    private final OrderMapper orderMapper;

    public Order createOrder(CreateOrderRequest request) {
        Set<String> allSymbols = symbolRepository.getAllSymbols();
        String symbol = request.getSymbol();

        if (!allSymbols.contains(symbol)) {
            throw new ResourceNotFoundException(String.format("Symbol '%s' is not exists", symbol));
        }

        LocalDateTime currentTime = LocalDateTime.now();

        Order order = orderMapper.toOrder(request);
        order.setStatus(OrderStatus.PENDING);
        order.setCreatedTime(currentTime);
        order.setUpdatedTime(currentTime);
        //todo get userId from spring security context holder and set to order.userId

        return orderRepository.createOrder(order);
    }

    public Order cancelOrder(Long orderId) {
        Order targetOrder = orderRepository.getOrderById(orderId);

        if (Objects.isNull(targetOrder)) {
            throw new ResourceNotFoundException(String.format("Order with id %d not found", orderId));
        }

        if (!OrderStatus.PENDING.equals(targetOrder.getStatus())) {
            throw new AppException(ErrorCode.BUSINESS_RULE_EXCEPTION, "Invalid order Status. Only PENDING orders can be canceled");
        }

        targetOrder.setStatus(OrderStatus.CANCELLED);
        targetOrder.setCanceledTime(LocalDateTime.now());
        //todo xóa khỏi queue chờ match order

        return orderRepository.updateOrder(targetOrder);
    }

    public List<Order> getAllOrder() {
        return orderRepository.getAllOrder();
    }

    public Order getOrderById(Long orderId) {
        return orderRepository.getOrderById(orderId);
    }
}
