package org.enolj.coffeeordersystem.domain.point.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.enolj.coffeeordersystem.common.entity.BaseEntity;
import org.enolj.coffeeordersystem.common.exception.ErrorEnum;
import org.enolj.coffeeordersystem.common.exception.ServiceErrorException;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "points")
public class Point extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long balance;

    @Column(nullable = false)
    private Long userId;

    public void charge(Long amount) {
        this.balance += amount;
    }

    public void use(Long amount) {
        if (this.balance < amount) {
            throw new ServiceErrorException(ErrorEnum.ERR_INSUFFICIENT_POINT);
        }
        this.balance -= amount;
    }
}
