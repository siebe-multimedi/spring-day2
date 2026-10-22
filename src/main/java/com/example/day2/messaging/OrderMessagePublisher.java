package com.example.day2.messaging;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Step 2: send the message AFTER the commit.
 * Since step 4 OrderService no longer publishes OrderPlaced: the outbox replaced this listener.
 */
@Component
public class OrderMessagePublisher {

    private final MessageBroker broker;

    public OrderMessagePublisher(MessageBroker broker) {
        this.broker = broker;
    }

    /** Only runs when the transaction that published the event has committed: a rollback sends nothing. */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(OrderPlaced event) {
        broker.send("orders", event.orderId());
    }
}
