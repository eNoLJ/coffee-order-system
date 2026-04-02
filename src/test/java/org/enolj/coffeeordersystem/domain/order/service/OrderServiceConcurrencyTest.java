package org.enolj.coffeeordersystem.domain.order.service;

import org.enolj.coffeeordersystem.domain.order.dto.OrderCreateRequest;
import org.enolj.coffeeordersystem.domain.order.repository.OrderRepository;
import org.enolj.coffeeordersystem.domain.outbox.repository.OutboxEventRepository;
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
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {
        "spring.task.scheduling.enabled=false",
        "spring.kafka.listener.auto-startup=false"
})
class OrderServiceConcurrencyTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private PointRepository pointRepository;

    @Autowired
    private PointHistoryRepository pointHistoryRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OutboxEventRepository outboxEventRepository;

    @Autowired
    private RedissonClient redissonClient;

    @AfterEach
    void tearDown() {
        redissonClient.getKeys().flushall();
    }

    @Test
    @DisplayName("동일 사용자가 동시에 여러 번 주문하면 Redis 분산 락으로 직렬화되어 잔액 범위 내에서만 성공해야 한다")
    void createOrder_withDistributedLock() throws InterruptedException {
        // given
        Long userId = 1L;
        Long menuId = 1L;
        long menuPrice = 3000L;

        long initialBalance = pointRepository.findByUserId(userId)
                .map(Point::getBalance)
                .orElseThrow();

        long initialUseHistoryCount = pointHistoryRepository.countByUserIdAndType(userId, PointHistoryType.USE);
        long initialOrderCount = orderRepository.count();
        long initialOutboxCount = outboxEventRepository.count();

        int threadCount = 5;

        long expectedSuccessCount = initialBalance / menuPrice;
        long expectedFinalBalance = initialBalance - (expectedSuccessCount * menuPrice);

        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch readyLatch = new CountDownLatch(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(threadCount);

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();

        List<Future<?>> futures = new ArrayList<>();

        // when
        for (int i = 0; i < threadCount; i++) {
            futures.add(executorService.submit(() -> {
                try {
                    readyLatch.countDown();
                    startLatch.await();

                    OrderCreateRequest request = new OrderCreateRequest();
                    ReflectionTestUtils.setField(request, "userId", userId);
                    ReflectionTestUtils.setField(request, "menuId", menuId);

                    orderService.createOrder(request);
                    successCount.incrementAndGet();

                } catch (Exception e) {
                    failCount.incrementAndGet();
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
                throw new RuntimeException("주문 동시성 테스트 중 예외 발생", e);
            }
        }

        executorService.shutdown();

        // then
        Point point = pointRepository.findByUserId(userId).orElseThrow();

        long finalUseHistoryCount = pointHistoryRepository.countByUserIdAndType(userId, PointHistoryType.USE);
        long finalOrderCount = orderRepository.count();
        long finalOutboxCount = outboxEventRepository.count();

        assertThat(successCount.get()).isEqualTo((int) expectedSuccessCount);
        assertThat(failCount.get()).isEqualTo(threadCount - (int) expectedSuccessCount);

        assertThat(point.getBalance()).isEqualTo(expectedFinalBalance);

        assertThat(finalUseHistoryCount).isEqualTo(initialUseHistoryCount + expectedSuccessCount);
        assertThat(finalOrderCount).isEqualTo(initialOrderCount + expectedSuccessCount);
        assertThat(finalOutboxCount).isEqualTo(initialOutboxCount + expectedSuccessCount);
    }
}
