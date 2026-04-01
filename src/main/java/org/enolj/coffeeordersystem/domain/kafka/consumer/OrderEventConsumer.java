package org.enolj.coffeeordersystem.domain.kafka.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.enolj.coffeeordersystem.domain.order.dto.OrderCreatedEventPayload;
import org.enolj.coffeeordersystem.domain.ordereventlog.entity.OrderEventLog;
import org.enolj.coffeeordersystem.domain.ordereventlog.repository.OrderEventLogRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventConsumer {

    private final ObjectMapper objectMapper;
    private final OrderEventLogRepository orderEventLogRepository;

    @Transactional
    @KafkaListener(
            topics = "coffee-order-events",
            groupId = "coffee-order-consumer-group"
    )
    public void consume(String message) {

        try {
            // 1. JSON → DTO 변환
            OrderCreatedEventPayload payload =
                    objectMapper.readValue(message, OrderCreatedEventPayload.class);

            // 2. 엔티티 생성
            OrderEventLog logEntity = OrderEventLog.from(
                    payload.getOrderId(),
                    payload.getUserId(),
                    payload.getMenuId(),
                    payload.getMenuName(),
                    payload.getPrice(),
                    payload.getOrderedAt()
            );

            // 3. DB 저장
            orderEventLogRepository.save(logEntity);

            log.info("주문 이벤트 저장 완료 - orderId: {}", payload.getOrderId());

        } catch (Exception e) {
            log.error("Kafka 메시지 처리 실패 - message: {}", message, e);
        }
    }
}
