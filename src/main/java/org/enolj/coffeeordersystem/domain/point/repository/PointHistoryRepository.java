package org.enolj.coffeeordersystem.domain.point.repository;

import org.enolj.coffeeordersystem.domain.point.entity.PointHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PointHistoryRepository extends JpaRepository<PointHistory, Long> {
}
