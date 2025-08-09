package com.equix.ordersimulator.domain.scheduler;

import com.equix.ordersimulator.domain.model.Order;
import com.equix.ordersimulator.domain.service.OrderMatchingEngine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;

@Component
@RequiredArgsConstructor
@Slf4j
public class MatchingJob {
    private final BlockingQueue<Order> orderQueue;
    private final OrderMatchingEngine matchingEngine;

    @Value("${application.matching-engine.enabled:true}")
    private boolean enabled;

    @Scheduled(fixedDelay = 1000)
    public void run() {
        if (!enabled) return;

        Order order;
        while ((order = orderQueue.poll()) != null) {
            matchingEngine.match(order);
        }
    }
}
