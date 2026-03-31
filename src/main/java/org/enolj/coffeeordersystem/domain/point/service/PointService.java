package org.enolj.coffeeordersystem.domain.point.service;

import lombok.RequiredArgsConstructor;
import org.enolj.coffeeordersystem.common.exception.ErrorEnum;
import org.enolj.coffeeordersystem.common.exception.ServiceErrorException;
import org.enolj.coffeeordersystem.domain.point.dto.PointChargeRequest;
import org.enolj.coffeeordersystem.domain.point.dto.PointChargeResponse;
import org.enolj.coffeeordersystem.domain.point.entity.Point;
import org.enolj.coffeeordersystem.domain.point.entity.PointHistory;
import org.enolj.coffeeordersystem.domain.point.entity.PointHistoryType;
import org.enolj.coffeeordersystem.domain.point.entity.ReferenceType;
import org.enolj.coffeeordersystem.domain.point.repository.PointHistoryRepository;
import org.enolj.coffeeordersystem.domain.point.repository.PointRepository;
import org.enolj.coffeeordersystem.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PointService {

    private final UserRepository userRepository;
    private final PointRepository pointRepository;
    private final PointHistoryRepository pointHistoryRepository;

    public PointChargeResponse charge(PointChargeRequest request) {
        Long userId = request.getUserId();
        Long amount = request.getAmount();

        if (amount == null || amount < 1) {
            throw new ServiceErrorException(ErrorEnum.ERR_INVALID_CHARGE_AMOUNT);
        }

        userRepository.findById(userId).orElseThrow(
                () -> new ServiceErrorException(ErrorEnum.ERR_NOT_FOUND_USER)
        );

        Point point = pointRepository.findByUserId(userId).orElseThrow(
                () -> new ServiceErrorException(ErrorEnum.ERR_NOT_FOUND_POINT)
        );

        point.charge(amount);

        PointHistory pointHistory = PointHistory.from(
                PointHistoryType.CHARGE,
                amount,
                point.getBalance(),
                ReferenceType.CHARGE,
                null,
                userId
        );
        pointHistoryRepository.save(pointHistory);

        return PointChargeResponse.from(userId, amount, point.getBalance());
    }
}
