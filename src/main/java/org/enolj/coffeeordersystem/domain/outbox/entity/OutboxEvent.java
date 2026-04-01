package org.enolj.coffeeordersystem.domain.outbox.entity;

import jakarta.persistence.*;
import lombok.*;
import org.enolj.coffeeordersystem.common.entity.BaseEntity;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(access = AccessLevel.PROTECTED)
@Table(name = "outbox_events")
public class OutboxEvent extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AggregateType aggregateType;

    @Column(nullable = false)
    private Long aggregateId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EventType eventType;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String payload;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OutboxEventStatus status;

    private LocalDateTime publishedAt;

    public static OutboxEvent from(Long orderId, String payload) {
        return OutboxEvent.builder()
                .aggregateType(AggregateType.ORDER)
                .aggregateId(orderId)
                .eventType(EventType.ORDER_CREATED)
                .payload(payload)
                .status(OutboxEventStatus.INIT)
                .build();
    }

    public void markPublished() {
        this.status = OutboxEventStatus.PUBLISHED;
        this.publishedAt = LocalDateTime.now();
    }

    public void markFailed() {
        this.status = OutboxEventStatus.FAILED;
    }
}
