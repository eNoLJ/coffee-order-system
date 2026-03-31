package org.enolj.coffeeordersystem.domain.point.entity;

import jakarta.persistence.*;
import lombok.*;
import org.enolj.coffeeordersystem.common.entity.BaseEntity;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(access = AccessLevel.PROTECTED)
@Table(name = "point_histories")
public class PointHistory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private PointHistoryType type;

    @Column(nullable = false)
    private Long amount;

    @Column(nullable = false)
    private Long balanceAfter;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private ReferenceType referenceType;

    private Long referenceId;

    @Column(nullable = false)
    private Long userId;

    public static PointHistory from(PointHistoryType type, Long amount, Long balanceAfter, ReferenceType referenceType, Long referenceId, Long userId) {
        return PointHistory.builder()
                .type(type)
                .amount(amount)
                .balanceAfter(balanceAfter)
                .referenceType(referenceType)
                .referenceId(referenceId)
                .userId(userId)
                .build();
    }
}
