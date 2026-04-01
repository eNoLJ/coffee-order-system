package org.enolj.coffeeordersystem.domain.point.service;

import org.enolj.coffeeordersystem.domain.point.dto.PointChargeRequest;
import org.enolj.coffeeordersystem.domain.point.entity.Point;
import org.enolj.coffeeordersystem.domain.point.entity.PointHistoryType;
import org.enolj.coffeeordersystem.domain.point.repository.PointHistoryRepository;
import org.enolj.coffeeordersystem.domain.point.repository.PointRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class PointServiceConcurrencyTest {

    @Autowired
    private PointService pointService;

    @Autowired
    private PointRepository pointRepository;

    @Autowired
    private PointHistoryRepository pointHistoryRepository;

    @Autowired
    private RedissonClient redissonClient;

    @AfterEach
    void tearDown() {
        redissonClient.getKeys().flushall();
    }

    @Test
    @DisplayName("포인트 충전 시 동일 사용자에 대한 동시 요청은 Redis 분산 락으로 직렬화되어야 한다")
    void charge_withDistributedLock() throws InterruptedException {
        // given
        Long userId = 1L;
        long initialBalance = pointRepository.findByUserId(userId)
                .map(Point::getBalance)
                .orElseThrow();

        long initialHistoryCount = pointHistoryRepository.countByUserIdAndType(
                userId,
                PointHistoryType.CHARGE
        );

        int threadCount = 10;
        long chargeAmount = 1000L;

        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch readyLatch = new CountDownLatch(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(threadCount);

        List<Future<?>> futures = new ArrayList<>();

        // when
        for (int i = 0; i < threadCount; i++) {
            futures.add(executorService.submit(() -> {
                try {
                    readyLatch.countDown();
                    startLatch.await();

                    PointChargeRequest chargeRequest = new PointChargeRequest();
                    ReflectionTestUtils.setField(chargeRequest, "userId", userId);
                    ReflectionTestUtils.setField(chargeRequest, "amount", chargeAmount);

                    pointService.charge(chargeRequest);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                } finally {
                    doneLatch.countDown();
                }
            }));
        }

        readyLatch.await();
        startLatch.countDown();
        doneLatch.await();

        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (ExecutionException e) {
                throw new RuntimeException("동시성 테스트 중 예외 발생", e);
            }
        }

        executorService.shutdown();

        // then
        Point point = pointRepository.findByUserId(userId).orElseThrow();
        long finalHistoryCount = pointHistoryRepository.countByUserIdAndType(
                userId,
                PointHistoryType.CHARGE
        );

        assertThat(point.getBalance())
                .isEqualTo(initialBalance + (threadCount * chargeAmount));

        assertThat(finalHistoryCount)
                .isEqualTo(initialHistoryCount + threadCount);
    }
}
