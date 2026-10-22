package com.example.day2.messaging;

import org.springframework.stereotype.Component;

/** Step 4: sends the messages that are waiting in the outbox. */
@Component
public class OutboxPublisher {

    private final OutboxRepository outbox;
    private final MessageBroker broker;

    public OutboxPublisher(OutboxRepository outbox, MessageBroker broker) {
        this.outbox = outbox;
        this.broker = broker;
    }

    /** In a real application this method runs on a schedule (@Scheduled). Here the tests call it. */
    public void publishPending() {
        for (OutboxMessage message : outbox.findUnsent()) {
            try {
                broker.send(message.getQueue(), message.getPayload());
            } catch (BrokerUnavailableException e) {
                System.out.println("[messaging] broker down, message " + message.getId() + " stays in the outbox");
                return;   // the next run tries again, in the same order
            }
            outbox.markSent(message.getId());
        }
    }
}
