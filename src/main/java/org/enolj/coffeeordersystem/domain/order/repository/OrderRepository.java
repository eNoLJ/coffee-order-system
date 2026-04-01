package org.enolj.coffeeordersystem.domain.order.repository;

import org.enolj.coffeeordersystem.domain.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
