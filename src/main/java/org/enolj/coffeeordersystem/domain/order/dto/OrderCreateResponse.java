package org.enolj.coffeeordersystem.domain.order.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(access = AccessLevel.PROTECTED)
public class OrderCreateResponse {

    private Long orderId;
    private Long userId;
    private Long menuId;
    private String menuName;
    private Long price;
    private Long remainingPoint;
    private String status;

    public static OrderCreateResponse from(Long orderId, Long userId, Long menuId, String menuName, Long price, Long remainingPoint, String status) {
        return OrderCreateResponse.builder()
                .orderId(orderId)
                .userId(userId)
                .menuId(menuId)
                .menuName(menuName)
                .price(price)
                .remainingPoint(remainingPoint)
                .status(status)
                .build();
    }
}
