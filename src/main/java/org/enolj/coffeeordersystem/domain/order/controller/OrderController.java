package org.enolj.coffeeordersystem.domain.order.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.enolj.coffeeordersystem.common.response.BaseResponse;
import org.enolj.coffeeordersystem.domain.order.dto.OrderCreateRequest;
import org.enolj.coffeeordersystem.domain.order.dto.OrderCreateResponse;
import org.enolj.coffeeordersystem.domain.order.service.OrderService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public BaseResponse<OrderCreateResponse> createOrder(@Valid @RequestBody OrderCreateRequest request) {
        return BaseResponse.success(
                "200",
                "커피 주문/결제 성공",
                orderService.createOrder(request)
        );
    }
}
