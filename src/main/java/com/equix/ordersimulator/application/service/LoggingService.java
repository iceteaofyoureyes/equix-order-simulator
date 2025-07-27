package com.equix.ordersimulator.application.service;

import com.equix.ordersimulator.application.dto.constant.OrderAction;
import com.equix.ordersimulator.domain.model.Order;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoggingService {

    private static final Logger businessLogger = LoggerFactory.getLogger("com.equix.ordersimulator.application");

    public void logOrderInfo(OrderAction action, Long orderId, String message) {
        businessLogger.info(
                "traceId={} | action={} | orderId={} | message={}",
                getTraceId(),
                action,
                orderId,
                message
        );
    }

    public void logCreateOrder(Order order, boolean success, String message) {
        businessLogger.info(
                "traceId={} | action={} | success={} | userId={} | symbol={} | price={} | quantity={} | side={} | orderId={} | message={}",
                getTraceId(),
                OrderAction.CREATE_ORDER,
                success,
                order.getUserId(),
                order.getSymbol(),
                order.getPrice(),
                order.getQuantity(),
                order.getSide(),
                order.getId(),
                message
        );
    }

    public void logCancelOrder(Order order, boolean success, String message) {
        businessLogger.info(
                "traceId={} | action={} | success={} | userId={} | symbol={} | orderId={} | message={}",
                getTraceId(),
                OrderAction.CANCEL_ORDER,
                success,
                order.getUserId(),
                order.getSymbol(),
                order.getId(),
                message
        );
    }

    public void logExecutedOrder(Order order, String message) {
        businessLogger.info(
                "traceId={} | action={} | userId={} | symbol={} | price={} | quantity={} | side={} | orderId={} | message={}",
                getTraceId(),
                OrderAction.EXECUTE_ORDER,
                order.getUserId(),
                order.getSymbol(),
                order.getPrice(),
                order.getQuantity(),
                order.getSide(),
                order.getId(),
                message
        );
    }

    private String getTraceId() {
        return MDC.get("traceId");
    }

}
