package com.equix.ordersimulator.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;

@Data
@AllArgsConstructor
public class SimulateExecutionResult {
    Integer executedOrdersNum;
    Set<Long> executedOrderIds;
}
