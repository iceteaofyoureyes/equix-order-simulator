package com.equix.ordersimulator.application.service;

import com.equix.ordersimulator.application.dto.SimulateExecutionResult;
import com.equix.ordersimulator.application.dto.constant.OrderAction;
import com.equix.ordersimulator.application.exception.AppException;
import com.equix.ordersimulator.application.exception.ResourceNotFoundException;
import com.equix.ordersimulator.domain.model.Order;
import com.equix.ordersimulator.domain.model.enums.OrderStatus;
import com.equix.ordersimulator.domain.repository.OrderRepository;
import com.equix.ordersimulator.domain.repository.SymbolRepository;
import com.equix.ordersimulator.interfaces.mapper.OrderMapper;
import com.equix.ordersimulator.interfaces.request.CreateOrderRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @InjectMocks
    OrderService orderService;

    @Mock
    OrderRepository orderRepository;

    @Mock
    SymbolRepository symbolRepository;

    @Mock
    OrderMapper orderMapper;

    @Mock
    LoggingService loggingService;

    @Nested
    @DisplayName("Tests for createOrder()")
    class CreateOrderTests {

        @Test
        void givenValidSymbol_whenCreateOrder_thenCreateOrderSuccessfully() {
            CreateOrderRequest request = new CreateOrderRequest();
            request.setSymbol("AAPL");
            when(symbolRepository.getAllSymbols()).thenReturn(Set.of("AAPL", "TSLA"));
            Order mappedOrder = new Order();
            when(orderMapper.toOrder(request)).thenReturn(mappedOrder);
            Order createdOrder = new Order();
            when(orderRepository.createOrder(any(Order.class))).thenReturn(createdOrder);

            Order result = orderService.createOrder(request);

            assertThat(result).isEqualTo(createdOrder);
            verify(orderRepository).createOrder(any(Order.class));
            verify(loggingService).logCreateOrder(eq(createdOrder), eq(true), anyString());
        }

        @Test
        void givenInvalidSymbol_whenCreateOrder_thenThrowException() {
            CreateOrderRequest request = new CreateOrderRequest();
            request.setSymbol("INVALID");
            when(symbolRepository.getAllSymbols()).thenReturn(Set.of("AAPL", "TSLA"));

            assertThatThrownBy(() -> orderService.createOrder(request))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Symbol");
            verify(loggingService).logOrderInfo(eq(OrderAction.CREATE_ORDER), isNull(), contains("failed"));
            verifyNoMoreInteractions(orderRepository);
        }
    }

    @Nested
    @DisplayName("Tests for cancelOrder()")
    class CancelOrderTests {

        @Test
        void givenValidPendingOrder_whenCancelOrder_thenSuccess() {
            Long orderId = 1L;
            Order pendingOrder = new Order();
            pendingOrder.setId(orderId);
            pendingOrder.setStatus(OrderStatus.PENDING);
            Order canceledOrder = new Order();
            when(orderRepository.getOrderById(orderId)).thenReturn(pendingOrder);
            when(orderRepository.updateOrder(any(Order.class))).thenReturn(canceledOrder);

            Order result = orderService.cancelOrder(orderId);

            assertThat(result).isEqualTo(canceledOrder);
            verify(orderRepository).updateOrder(any(Order.class));
            verify(loggingService).logCancelOrder(eq(canceledOrder), eq(true), anyString());
        }

        @Test
        void givenNonExistingOrder_whenCancelOrder_thenThrow() {
            Long orderId = 99L;
            when(orderRepository.getOrderById(orderId)).thenReturn(null);

            assertThatThrownBy(() -> orderService.cancelOrder(orderId))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("not found");
            verify(loggingService).logOrderInfo(eq(OrderAction.CANCEL_ORDER), eq(orderId), contains("not found"));
        }

        @Test
        void givenNonPendingOrder_whenCancelOrder_thenThrow() {
            Long orderId = 1L;
            Order executedOrder = new Order();
            executedOrder.setId(orderId);
            executedOrder.setStatus(OrderStatus.EXECUTED);
            when(orderRepository.getOrderById(orderId)).thenReturn(executedOrder);

            assertThatThrownBy(() -> orderService.cancelOrder(orderId))
                    .isInstanceOf(AppException.class)
                    .hasMessageContaining("Only PENDING orders");
            verify(loggingService).logCancelOrder(eq(executedOrder), eq(false), contains("failed"));
        }
    }

    @Test
    void whenGetAll_shouldReturnList() {
        List<Order> mockOrders = List.of(
                Order.builder().id(1L).build(),
                Order.builder().id(2L).build()
        );
        when(orderRepository.getAllOrder()).thenReturn(mockOrders);

        List<Order> actualOrders = orderService.getAllOrder();

        assertThat(actualOrders).hasSize(2);
        verify(orderRepository).getAllOrder();
    }

    @Test
    void givenExistingId_whenGetOrderById_thenReturnOrder() {
        Long id = 1L;
        Order order = new Order();
        order.setId(id);
        when(orderRepository.getOrderById(id)).thenReturn(order);

        Order result = orderService.getOrderById(id);

        assertThat(result).isEqualTo(order);
    }

    @Test
    void givenNonExistingId_whenGetOrderById_thenThrow() {
        Long id = 1L;
        when(orderRepository.getOrderById(id)).thenReturn(null);

        assertThatThrownBy(() -> orderService.getOrderById(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("not found");
    }

    @Test
    void givenNoPendingOrders_whenSimulateExecution_thenThrow() {
        when(orderRepository.getOrdersByStatus(OrderStatus.PENDING)).thenReturn(Collections.emptyList());

        assertThatThrownBy(() -> orderService.simulateExecution())
                .isInstanceOf(AppException.class)
                .hasMessageContaining("No PENDING Order");
        verify(loggingService).logOrderInfo(eq(OrderAction.EXECUTE_ORDER), isNull(), contains("No PENDING Order"));
    }

    @Test
    void givenPendingOrders_whenSimulateExecution_thenUpdateStatus() {
        Order o1 = new Order();
        o1.setId(1L);
        Order o2 = new Order();
        o2.setId(2L);
        when(orderRepository.getOrdersByStatus(OrderStatus.PENDING)).thenReturn(List.of(o1, o2));
        when(orderRepository.updateOrdersStatus(anySet(), eq(OrderStatus.EXECUTED)))
                .thenReturn(List.of(o1)); // assert that 1 Order is executed

        SimulateExecutionResult result = orderService.simulateExecution();

        assertThat(result).isNotNull();
        assertThat(result.getExecutedOrdersNum()).isGreaterThanOrEqualTo(0);
        assertThat(result.getExecutedOrderIds()).isNotNull();
        verify(orderRepository).updateOrdersStatus(anySet(), eq(OrderStatus.EXECUTED));
        verify(loggingService).logOrderInfo(eq(OrderAction.EXECUTE_ORDER), isNull(), contains("Updated"));
    }
}
