package com.equix.ordersimulator.domain.service;

import com.equix.ordersimulator.domain.model.Order;
import com.equix.ordersimulator.domain.model.PriceLevel;
import com.equix.ordersimulator.domain.model.enums.OrderSide;
import com.equix.ordersimulator.domain.model.enums.OrderStatus;
import com.equix.ordersimulator.domain.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderMatchingEngine {

    private final OrderRepository orderRepository;

    public void match(Order incomingOrder) {
        if (incomingOrder.getSide() == OrderSide.BUY) {
            matchBuy(incomingOrder);
        } else {
            matchSell(incomingOrder);
        }
    }

    private void matchBuy(Order buyOrder) {
        while (true) {
            String symbol = buyOrder.getSymbol();
            Optional<PriceLevel> maybeBestAsk = orderRepository.getBestAsk(symbol);
            if (maybeBestAsk.isEmpty()) break;

            PriceLevel bestAskLevel = maybeBestAsk.get();

            if (buyOrder.getPrice().compareTo(bestAskLevel.getPrice()) < 0) break;

            while (!bestAskLevel.isEmpty() && buyOrder.getRemainingQuantity() > 0) {
                Order sellOrder = bestAskLevel.peek();

                Integer matchQty = Math.min(buyOrder.getRemainingQuantity(), sellOrder.getRemainingQuantity());
                buyOrder.fill(matchQty);
                sellOrder.fill(matchQty);

                log.info(
                        "EXECUTION | BUY Order#{} | SELL Order#{} | Qty={} | Price={} | BUY Remaining={} | SELL Remaining={}",
                        buyOrder.getId(),
                        sellOrder.getId(),
                        matchQty,
                        bestAskLevel.getPrice(),
                        buyOrder.getRemainingQuantity(),
                        sellOrder.getRemainingQuantity()
                );

                if (sellOrder.isFilled()) {
                    bestAskLevel.poll();
                    sellOrder.setStatus(OrderStatus.EXECUTED);
                    orderRepository.removeFromBook(sellOrder);
                }
                orderRepository.updateOrder(sellOrder);

                if (buyOrder.isFilled()) {
                    buyOrder.setStatus(OrderStatus.EXECUTED);
                    orderRepository.updateOrder(buyOrder);
                    orderRepository.removeFromBook(buyOrder);
                    return;
                }
            }

            if (bestAskLevel.isEmpty()) {
                orderRepository.removePriceLevel(symbol, OrderSide.SELL, bestAskLevel.getPrice());
            }
        }

        if (!buyOrder.isFilled()) {
            orderRepository.addToBook(buyOrder);
            orderRepository.updateOrder(buyOrder);
        }
    }

    private void matchSell(Order sellOrder) {
        while (true) {
            String symbol = sellOrder.getSymbol();
            Optional<PriceLevel> maybeBestBid = orderRepository.getBestBidByOrderBook(symbol);
            if (maybeBestBid.isEmpty()) break;

            PriceLevel bestBidLevel = maybeBestBid.get();
            if (sellOrder.getPrice().compareTo(bestBidLevel.getPrice()) > 0) break;

            while (!bestBidLevel.isEmpty() && sellOrder.getRemainingQuantity() > 0) {
                Order buyOrder = bestBidLevel.peek();

                Integer matchQty = Math.min(sellOrder.getRemainingQuantity(), buyOrder.getRemainingQuantity());
                sellOrder.fill(matchQty);
                buyOrder.fill(matchQty);

                log.info(
                        "EXECUTION | SELL Order#{} | BUY Order#{} | Qty={} | Price={} | SELL Remaining={} | BUY Remaining={}",
                        sellOrder.getId(),
                        buyOrder.getId(),
                        matchQty,
                        bestBidLevel.getPrice(),
                        sellOrder.getRemainingQuantity(),
                        buyOrder.getRemainingQuantity()
                );

                if (buyOrder.isFilled()) {
                    bestBidLevel.poll();
                    buyOrder.setStatus(OrderStatus.EXECUTED);
                    orderRepository.removeFromBook(buyOrder);
                }
                orderRepository.updateOrder(buyOrder);

                if (sellOrder.isFilled()) {
                    sellOrder.setStatus(OrderStatus.EXECUTED);
                    orderRepository.updateOrder(sellOrder);
                    orderRepository.removeFromBook(sellOrder);
                    return;
                }
            }

            if (bestBidLevel.isEmpty()) {
                orderRepository.removePriceLevel(symbol, OrderSide.BUY, bestBidLevel.getPrice());
            }
        }

        if (!sellOrder.isFilled()) {
            orderRepository.addToBook(sellOrder);
            orderRepository.updateOrder(sellOrder);
        }
    }
}
