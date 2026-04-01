package org.enolj.coffeeordersystem.domain.ordereventlog.entity;

import jakarta.persistence.*;
import lombok.*;
import org.enolj.coffeeordersystem.common.entity.BaseEntity;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(access = AccessLevel.PROTECTED)
@Table(name = "order_event_logs")
public class OrderEventLog extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long orderId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long menuId;

    @Column(nullable = false)
    private String menuName;

    @Column(nullable = false)
    private Long price;

    @Column(nullable = false)
    private LocalDateTime orderedAt;

    public static OrderEventLog from(Long orderId, Long userId, Long menuId, String menuName, Long price, LocalDateTime orderedAt) {
        return OrderEventLog.builder()
                .orderId(orderId)
                .userId(userId)
                .menuId(menuId)
                .menuName(menuName)
                .price(price)
                .orderedAt(orderedAt)
                .build();
    }
}
