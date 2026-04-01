package org.enolj.coffeeordersystem.domain.order.service;

import lombok.RequiredArgsConstructor;
import org.enolj.coffeeordersystem.common.redis.RedisLockService;
import org.enolj.coffeeordersystem.domain.order.dto.OrderCreateRequest;
import org.enolj.coffeeordersystem.domain.order.dto.OrderCreateResponse;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final RedisLockService redisLockService;
    private final OrderTransactionService orderTransactionService;

    public OrderCreateResponse createOrder(OrderCreateRequest request) {
        Long userId = request.getUserId();
        Long menuId = request.getMenuId();

        String lockKey = "lock:point:user:" + userId;

        return redisLockService.executeWithLock(
                lockKey,
                3L,
                5L,
                () -> orderTransactionService.createOrder(userId, menuId)
        );
    }
}
