package org.enolj.coffeeordersystem.domain.order.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(access = AccessLevel.PROTECTED)
public class OrderCreatedEventPayload {

    private Long orderId;
    private Long userId;
    private Long menuId;
    private String menuName;
    private Long price;
    private LocalDateTime orderedAt;

    public static OrderCreatedEventPayload from(Long orderId, Long userId, Long menuId, String menuName, Long price, LocalDateTime orderedAt) {
        return OrderCreatedEventPayload.builder()
                .orderId(orderId)
                .userId(userId)
                .menuId(menuId)
                .menuName(menuName)
                .price(price)
                .orderedAt(orderedAt)
                .build();
    }
}
