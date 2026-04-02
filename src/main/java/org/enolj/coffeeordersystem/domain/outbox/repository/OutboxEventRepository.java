package org.enolj.coffeeordersystem.domain.outbox.repository;

import org.enolj.coffeeordersystem.domain.outbox.entity.OutboxEvent;
import org.enolj.coffeeordersystem.domain.outbox.entity.OutboxEventStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent, Long> {

    List<OutboxEvent> findTop100ByStatusOrderByIdAsc(OutboxEventStatus status);

    Optional<OutboxEvent> findTopByOrderByIdDesc();
}
