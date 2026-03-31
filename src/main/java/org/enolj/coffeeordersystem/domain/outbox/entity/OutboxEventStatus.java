package org.enolj.coffeeordersystem.domain.outbox.entity;

public enum OutboxEventStatus {

    INIT,
    PUBLISHED,
    FAILED,
    RETRYING
}
