package org.enolj.coffeeordersystem.domain.point.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(access = AccessLevel.PROTECTED)
public class PointChargeResponse {

    private Long userId;
    private Long chargeAmount;
    private Long currentBalance;

    public static PointChargeResponse from(Long userId, Long chargeAmount, Long currentBalance) {
        return PointChargeResponse.builder()
                .userId(userId)
                .chargeAmount(chargeAmount)
                .currentBalance(currentBalance)
                .build();
    }
}
