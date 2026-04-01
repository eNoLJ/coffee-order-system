package org.enolj.coffeeordersystem.domain.point.service;

import lombok.RequiredArgsConstructor;
import org.enolj.coffeeordersystem.common.exception.ErrorEnum;
import org.enolj.coffeeordersystem.common.exception.ServiceErrorException;
import org.enolj.coffeeordersystem.common.redis.RedisLockService;
import org.enolj.coffeeordersystem.domain.point.dto.PointChargeRequest;
import org.enolj.coffeeordersystem.domain.point.dto.PointChargeResponse;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PointService {

    private final RedisLockService redisLockService;
    private final PointTransactionService pointTransactionService;

    public PointChargeResponse charge(PointChargeRequest request) {
        Long userId = request.getUserId();
        Long amount = request.getAmount();

        if (amount == null || amount < 1) {
            throw new ServiceErrorException(ErrorEnum.ERR_INVALID_CHARGE_AMOUNT);
        }

        String lockKey = "lock:point:user:" + userId;

        return redisLockService.executeWithLock(lockKey, 3L, 5L,
                () -> pointTransactionService.charge(userId, amount));
    }
}
