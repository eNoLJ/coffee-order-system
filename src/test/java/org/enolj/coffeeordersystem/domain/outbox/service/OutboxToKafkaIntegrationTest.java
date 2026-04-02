package org.enolj.coffeeordersystem.domain.outbox.service;

import org.enolj.coffeeordersystem.domain.ordereventlog.entity.OrderEventLog;
import org.enolj.coffeeordersystem.domain.ordereventlog.repository.OrderEventLogRepository;
import org.enolj.coffeeordersystem.domain.order.dto.OrderCreateRequest;
import org.enolj.coffeeordersystem.domain.order.dto.OrderCreateResponse;
import org.enolj.coffeeordersystem.domain.order.service.OrderService;
import org.enolj.coffeeordersystem.domain.outbox.entity.EventType;
import org.enolj.coffeeordersystem.domain.outbox.entity.OutboxEvent;
import org.enolj.coffeeordersystem.domain.outbox.entity.OutboxEventStatus;
import org.enolj.coffeeordersystem.domain.outbox.repository.OutboxEventRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {
        "spring.task.scheduling.enabled=false",
        "spring.kafka.consumer.auto-offset-reset=earliest",
        "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}"
})
@EmbeddedKafka(partitions = 1, topics = {"coffee-order-events"})
class OutboxToKafkaIntegrationTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OutboxPublisher outboxPublisher;

    @Autowired
    private OutboxEventRepository outboxEventRepository;

    @Autowired
    private OrderEventLogRepository orderEventLogRepository;

    @Test
    @DisplayName("Outbox 이벤트는 Publisher를 통해 Kafka로 발행되고 Consumer가 수신하여 order_event_logs에 저장한다")
    void outbox_to_kafka_to_consumer_success() throws Exception {
        // given
        Long userId = 1L;
        Long menuId = 1L;

        long initialOutboxCount = outboxEventRepository.count();
        long initialEventLogCount = orderEventLogRepository.count();

        OrderCreateRequest request = createRequest(userId, menuId);

        // when 1. 주문/결제 수행 -> Outbox INIT 저장
        OrderCreateResponse response = orderService.createOrder(request);

        // then 1. Outbox INIT 상태 저장 확인
        assertThat(outboxEventRepository.count()).isEqualTo(initialOutboxCount + 1);

        OutboxEvent outboxEvent = outboxEventRepository.findTopByOrderByIdDesc()
                .orElseThrow();

        assertThat(outboxEvent.getAggregateId()).isEqualTo(response.getOrderId());
        assertThat(outboxEvent.getEventType()).isEqualTo(EventType.ORDER_CREATED);
        assertThat(outboxEvent.getStatus()).isEqualTo(OutboxEventStatus.INIT);

        // when 2. Publisher 수동 실행 -> Kafka 발행
        outboxPublisher.publish();

        // Kafka Consumer 비동기 처리 대기
        Thread.sleep(2000);

        // then 2. Outbox 상태 변경 확인
        OutboxEvent publishedEvent = outboxEventRepository.findById(outboxEvent.getId())
                .orElseThrow();

        assertThat(publishedEvent.getStatus()).isEqualTo(OutboxEventStatus.PUBLISHED);
        assertThat(publishedEvent.getPublishedAt()).isNotNull();

        // then 3. Consumer가 order_event_logs 저장했는지 확인
        List<OrderEventLog> logs = orderEventLogRepository.findAll();
        assertThat(logs.size()).isEqualTo(initialEventLogCount + 1);

        OrderEventLog latestLog = logs.get(logs.size() - 1);

        assertThat(latestLog.getOrderId()).isEqualTo(response.getOrderId());
        assertThat(latestLog.getUserId()).isEqualTo(userId);
        assertThat(latestLog.getMenuId()).isEqualTo(menuId);
        assertThat(latestLog.getMenuName()).isEqualTo(response.getMenuName());
        assertThat(latestLog.getPrice()).isEqualTo(response.getPrice());
    }

    private OrderCreateRequest createRequest(Long userId, Long menuId) {
        OrderCreateRequest request = new OrderCreateRequest();
        ReflectionTestUtils.setField(request, "userId", userId);
        ReflectionTestUtils.setField(request, "menuId", menuId);
        return request;
    }
}
