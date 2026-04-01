package org.enolj.coffeeordersystem.domain.kafka.producer;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderEventProducer {

    private static final String TOPIC = "coffee-order-events";

    private final KafkaTemplate<String, String> kafkaTemplate;

    public void send(String key, String payload) {
        kafkaTemplate.send(TOPIC, key, payload);
    }
}
