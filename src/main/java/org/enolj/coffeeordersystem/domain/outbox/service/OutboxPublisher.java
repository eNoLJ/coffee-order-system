package org.enolj.coffeeordersystem.domain.outbox.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.enolj.coffeeordersystem.domain.kafka.producer.OrderEventProducer;
import org.enolj.coffeeordersystem.domain.outbox.entity.OutboxEvent;
import org.enolj.coffeeordersystem.domain.outbox.entity.OutboxEventStatus;
import org.enolj.coffeeordersystem.domain.outbox.repository.OutboxEventRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxPublisher {

    private final OutboxEventRepository outboxEventRepository;
    private final OrderEventProducer orderEventProducer;

    @Scheduled(fixedDelay = 3000)
    @Transactional
    public void publish() {
        List<OutboxEvent> events = outboxEventRepository.findTop100ByStatusOrderByIdAsc(OutboxEventStatus.INIT);

        for (OutboxEvent event : events) {
            try {
                orderEventProducer.send(String.valueOf(event.getAggregateId()), event.getPayload());
                event.markPublished();
                log.info("Outbox 이벤트 발행 성공 - outboxId: {}, aggregateId: {}", event.getId(), event.getAggregateId());
            } catch (Exception e) {
                event.markFailed();
                log.error("Outbox 이벤트 발행 실패 - outboxId: {}", event.getId(), e);
            }
        }
    }
}
