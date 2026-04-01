package org.enolj.coffeeordersystem.domain.order.service;

import lombok.RequiredArgsConstructor;
import org.enolj.coffeeordersystem.common.exception.ErrorEnum;
import org.enolj.coffeeordersystem.common.exception.ServiceErrorException;
import org.enolj.coffeeordersystem.domain.menu.entity.Menu;
import org.enolj.coffeeordersystem.domain.menu.entity.MenuStatus;
import org.enolj.coffeeordersystem.domain.menu.repository.MenuRepository;
import org.enolj.coffeeordersystem.domain.order.dto.OrderCreateResponse;
import org.enolj.coffeeordersystem.domain.order.dto.OrderCreatedEventPayload;
import org.enolj.coffeeordersystem.domain.order.entity.Order;
import org.enolj.coffeeordersystem.domain.order.repository.OrderRepository;
import org.enolj.coffeeordersystem.domain.outbox.entity.OutboxEvent;
import org.enolj.coffeeordersystem.domain.outbox.repository.OutboxEventRepository;
import org.enolj.coffeeordersystem.domain.point.entity.Point;
import org.enolj.coffeeordersystem.domain.point.entity.PointHistory;
import org.enolj.coffeeordersystem.domain.point.entity.PointHistoryType;
import org.enolj.coffeeordersystem.domain.point.entity.ReferenceType;
import org.enolj.coffeeordersystem.domain.point.repository.PointHistoryRepository;
import org.enolj.coffeeordersystem.domain.point.repository.PointRepository;
import org.enolj.coffeeordersystem.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
public class OrderTransactionService {

    private final UserRepository userRepository;
    private final MenuRepository menuRepository;
    private final PointRepository pointRepository;
    private final PointHistoryRepository pointHistoryRepository;
    private final OrderRepository orderRepository;
    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public OrderCreateResponse createOrder(Long userId, Long menuId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ServiceErrorException(ErrorEnum.ERR_NOT_FOUND_USER));

        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new ServiceErrorException(ErrorEnum.ERR_NOT_FOUND_MENU));

        if (menu.getStatus() != MenuStatus.ON_SALE) {
            throw new ServiceErrorException(ErrorEnum.ERR_MENU_NOT_ON_SALE);
        }

        Point point = pointRepository.findByUserId(userId)
                .orElseThrow(() -> new ServiceErrorException(ErrorEnum.ERR_NOT_FOUND_POINT));

        if (point.getBalance() < menu.getPrice()) {
            throw new ServiceErrorException(ErrorEnum.ERR_INSUFFICIENT_POINT);
        }

        point.use(menu.getPrice());

        PointHistory pointHistory = PointHistory.from(
                PointHistoryType.USE,
                menu.getPrice(),
                point.getBalance(),
                ReferenceType.ORDER,
                null,
                userId
        );
        pointHistoryRepository.save(pointHistory);

        Order order = Order.from(
                menu.getName(),
                menu.getPrice(),
                userId,
                menuId
        );
        Order savedOrder = orderRepository.save(order);

        OrderCreatedEventPayload payload = OrderCreatedEventPayload.from(
                savedOrder.getId(),
                userId,
                menuId,
                savedOrder.getMenuName(),
                savedOrder.getPrice(),
                savedOrder.getOrderedAt()
        );

        try {
            String payloadJson = objectMapper.writeValueAsString(payload);
            OutboxEvent outboxEvent = OutboxEvent.from(savedOrder.getId(), payloadJson);
            outboxEventRepository.save(outboxEvent);
        } catch (Exception e) {
            throw new RuntimeException("Outbox payload 직렬화 실패", e);
        }

        return OrderCreateResponse.from(
                savedOrder.getId(),
                userId,
                menuId,
                savedOrder.getMenuName(),
                savedOrder.getPrice(),
                point.getBalance(),
                savedOrder.getStatus().name()
        );
    }
}
