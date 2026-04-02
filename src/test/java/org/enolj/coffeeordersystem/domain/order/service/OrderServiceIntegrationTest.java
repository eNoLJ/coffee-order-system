package org.enolj.coffeeordersystem.domain.order.service;

import org.enolj.coffeeordersystem.common.exception.ServiceErrorException;
import org.enolj.coffeeordersystem.domain.order.dto.OrderCreateRequest;
import org.enolj.coffeeordersystem.domain.order.dto.OrderCreateResponse;
import org.enolj.coffeeordersystem.domain.order.entity.Order;
import org.enolj.coffeeordersystem.domain.order.entity.OrderStatus;
import org.enolj.coffeeordersystem.domain.order.repository.OrderRepository;
import org.enolj.coffeeordersystem.domain.outbox.entity.EventType;
import org.enolj.coffeeordersystem.domain.outbox.entity.OutboxEvent;
import org.enolj.coffeeordersystem.domain.outbox.entity.OutboxEventStatus;
import org.enolj.coffeeordersystem.domain.outbox.repository.OutboxEventRepository;
import org.enolj.coffeeordersystem.domain.point.entity.Point;
import org.enolj.coffeeordersystem.domain.point.entity.PointHistoryType;
import org.enolj.coffeeordersystem.domain.point.repository.PointHistoryRepository;
import org.enolj.coffeeordersystem.domain.point.repository.PointRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(properties = {
        "spring.task.scheduling.enabled=false",
        "spring.kafka.listener.auto-startup=false"
})
class OrderServiceIntegrationTest {

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

    @Test
    @DisplayName("주문/결제 성공 시 포인트가 차감되고 주문, 포인트이력, 아웃박스 이벤트가 저장된다")
    void createOrder_success() {
        // given
        Long userId = 1L;
        Long menuId = 1L;

        long initialBalance = pointRepository.findByUserId(userId)
                .map(Point::getBalance)
                .orElseThrow();

        long initialUseHistoryCount = pointHistoryRepository.countByUserIdAndType(userId, PointHistoryType.USE);
        long initialOrderCount = orderRepository.count();
        long initialOutboxCount = outboxEventRepository.count();

        OrderCreateRequest request = createRequest(userId, menuId);

        // when
        OrderCreateResponse response = orderService.createOrder(request);

        // then
        Point point = pointRepository.findByUserId(userId).orElseThrow();
        Order savedOrder = orderRepository.findById(response.getOrderId()).orElseThrow();

        assertThat(response.getUserId()).isEqualTo(userId);
        assertThat(response.getMenuId()).isEqualTo(menuId);
        assertThat(response.getStatus()).isEqualTo("PAID");

        assertThat(savedOrder.getStatus()).isEqualTo(OrderStatus.PAID);
        assertThat(savedOrder.getUserId()).isEqualTo(userId);
        assertThat(savedOrder.getMenuId()).isEqualTo(menuId);

        assertThat(point.getBalance())
                .isEqualTo(initialBalance - savedOrder.getPrice());

        assertThat(pointHistoryRepository.countByUserIdAndType(userId, PointHistoryType.USE))
                .isEqualTo(initialUseHistoryCount + 1);

        assertThat(orderRepository.count())
                .isEqualTo(initialOrderCount + 1);

        assertThat(outboxEventRepository.count())
                .isEqualTo(initialOutboxCount + 1);

        OutboxEvent outboxEvent = outboxEventRepository.findTopByOrderByIdDesc()
                .orElseThrow();

        assertThat(outboxEvent.getAggregateId()).isEqualTo(savedOrder.getId());
        assertThat(outboxEvent.getEventType()).isEqualTo(EventType.ORDER_CREATED);
        assertThat(outboxEvent.getStatus()).isEqualTo(OutboxEventStatus.INIT);
        assertThat(outboxEvent.getPayload()).contains("\"orderId\":" + savedOrder.getId());
        assertThat(outboxEvent.getPayload()).contains("\"userId\":" + userId);
    }

    @Test
    @DisplayName("포인트가 부족하면 주문/결제에 실패하고 주문과 아웃박스 이벤트는 저장되지 않는다")
    void createOrder_fail_insufficientPoint() {
        // given
        Long userId = 3L;   // data.sql에서 포인트 0인 유저라고 가정
        Long menuId = 1L;

        long initialBalance = pointRepository.findByUserId(userId)
                .map(Point::getBalance)
                .orElseThrow();

        long initialOrderCount = orderRepository.count();
        long initialOutboxCount = outboxEventRepository.count();
        long initialUseHistoryCount = pointHistoryRepository.countByUserIdAndType(userId, PointHistoryType.USE);

        OrderCreateRequest request = createRequest(userId, menuId);

        // when & then
        assertThatThrownBy(() -> orderService.createOrder(request))
                .isInstanceOf(ServiceErrorException.class)
                .hasMessageContaining("포인트가 부족");

        Point point = pointRepository.findByUserId(userId).orElseThrow();

        assertThat(point.getBalance()).isEqualTo(initialBalance);
        assertThat(orderRepository.count()).isEqualTo(initialOrderCount);
        assertThat(outboxEventRepository.count()).isEqualTo(initialOutboxCount);
        assertThat(pointHistoryRepository.countByUserIdAndType(userId, PointHistoryType.USE))
                .isEqualTo(initialUseHistoryCount);
    }

    @Test
    @DisplayName("판매 중이 아닌 메뉴는 주문할 수 없다")
    void createOrder_fail_menuNotOnSale() {
        // given
        Long userId = 1L;
        Long menuId = 9L;   // data.sql에서 SOLD_OUT 또는 HIDDEN 메뉴라고 가정

        long initialOrderCount = orderRepository.count();
        long initialOutboxCount = outboxEventRepository.count();

        OrderCreateRequest request = createRequest(userId, menuId);

        // when & then
        assertThatThrownBy(() -> orderService.createOrder(request))
                .isInstanceOf(ServiceErrorException.class)
                .hasMessageContaining("주문할 수 없는 메뉴");

        assertThat(orderRepository.count()).isEqualTo(initialOrderCount);
        assertThat(outboxEventRepository.count()).isEqualTo(initialOutboxCount);
    }

    private OrderCreateRequest createRequest(Long userId, Long menuId) {
        OrderCreateRequest request = new OrderCreateRequest();
        ReflectionTestUtils.setField(request, "userId", userId);
        ReflectionTestUtils.setField(request, "menuId", menuId);
        return request;
    }
}
