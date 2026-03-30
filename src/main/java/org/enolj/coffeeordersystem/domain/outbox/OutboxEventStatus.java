package org.enolj.coffeeordersystem.domain.outbox;

public enum OutboxEventStatus {

    INIT,
    PUBLISHED,
    FAILED,
    RETRYING
}
