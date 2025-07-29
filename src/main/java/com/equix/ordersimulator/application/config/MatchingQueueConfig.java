package com.equix.ordersimulator.application.config;

import com.equix.ordersimulator.domain.model.Order;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Configuration
public class MatchingQueueConfig {
    @Bean
    public BlockingQueue<Order> orderQueue() {
        return new LinkedBlockingQueue<>();
    }
}
