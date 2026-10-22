package com.example.day2.messaging;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** Bonus: the consumer of the messages. */
@Component
public class OrderListener {

    private final Warehouse warehouse;
    private final ProcessedMessageRepository processed;

    public OrderListener(Warehouse warehouse, ProcessedMessageRepository processed) {
        this.warehouse = warehouse;
        this.processed = processed;
    }

    /** In a real application this is a message listener (with Spring AMQP: @RabbitListener). */
    @Transactional
    public void handle(String messageId, Long orderId) {
        if (processed.existsById(messageId)) {
            System.out.println("[messaging] duplicate message " + messageId + " ignored");
            return;
        }
        // same transaction as the work: if the work fails, the id is not stored and a redelivery is handled again
        processed.save(new ProcessedMessage(messageId));
        warehouse.reserve(orderId);
    }
}
