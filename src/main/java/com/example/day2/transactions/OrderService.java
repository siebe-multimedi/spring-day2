package com.example.day2.transactions;

import com.example.day2.shop.Order;
import com.example.day2.shop.OrderRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {

    private final OrderRepository orders;
    private final AuditService audit;

    public OrderService(OrderRepository orders, AuditService audit) {
        this.orders = orders;
        this.audit = audit;
    }

    @Transactional(rollbackFor = OrderRejectedException.class)
    public void placeOrder(Order order) throws OrderRejectedException {
        try {
            audit.log("placing order");
        } catch (RuntimeException e) {
            // the audit runs in its own transaction: its failure must not stop the order
            System.out.println("[transactions] audit failed, the order goes on: " + e.getMessage());
        }
        orders.save(order);
        if (order.getAmount() > 1000) {
            throw new OrderRejectedException("Amount too high!");
        }
    }
}
