package org.enolj.coffeeordersystem.domain.ordereventlog.repository;

import org.enolj.coffeeordersystem.domain.ordereventlog.entity.OrderEventLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderEventLogRepository extends JpaRepository<OrderEventLog, Long> {
}
