package org.enolj.coffeeordersystem.domain.point.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.enolj.coffeeordersystem.common.response.BaseResponse;
import org.enolj.coffeeordersystem.domain.point.dto.PointChargeRequest;
import org.enolj.coffeeordersystem.domain.point.dto.PointChargeResponse;
import org.enolj.coffeeordersystem.domain.point.service.PointService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/points")
public class PointController {

    private final PointService pointService;

    @PostMapping("/charge")
    public BaseResponse<org.enolj.coffeeordersystem.domain.point.dto.PointChargeResponse> charge(@Valid @RequestBody PointChargeRequest request) {
        PointChargeResponse response =  pointService.charge(request);
        return BaseResponse.success("200", "포인트 충전 성공", response);
    }
}
