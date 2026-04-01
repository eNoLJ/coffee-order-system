package org.enolj.coffeeordersystem.common.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.enolj.coffeeordersystem.common.exception.ErrorEnum;
import org.enolj.coffeeordersystem.common.exception.ServiceErrorException;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisLockService {

    private final RedissonClient redissonClient;

    public <T> T executeWithLock(String key, long waitTime, long leaseTime, Supplier<T> supplier) {
        RLock lock = redissonClient.getLock(key);

        try {
            boolean available = lock.tryLock(waitTime, leaseTime, TimeUnit.SECONDS);

            if (!available) {
                throw new ServiceErrorException(ErrorEnum.ERR_LOCK_ACQUIRE_FAILED);
            }

            return supplier.get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ServiceErrorException(ErrorEnum.ERR_LOCK_ACQUIRE_FAILED);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
                log.info("락 해제 완료 - thread: {}, key: {}", Thread.currentThread().getName(), key);
            }
        }
    }
}
