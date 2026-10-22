package com.example.day2.messaging;

import com.example.day2.shop.Order;
import com.example.day2.shop.OrderRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {

    private final OrderRepository orders;
    private final OutboxRepository outbox;

    public OrderService(OrderRepository orders, OutboxRepository outbox) {
        this.orders = orders;
        this.outbox = outbox;
    }

    @Transactional
    public void placeOrder(Order order) {
        orders.save(order);
        // Step 4: the message is a row in the same transaction: both are committed, or neither.
        // OutboxPublisher sends it later. (This replaces the OrderPlaced event of step 2.)
        outbox.save(new OutboxMessage("orders", order.getId()));
        if (order.getAmount() > 1000) {
            throw new IllegalStateException("Amount too high!");
        }
    }
}
