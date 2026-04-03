package org.enolj.coffeeordersystem.domain.menu.controller;

import lombok.RequiredArgsConstructor;
import org.enolj.coffeeordersystem.common.response.BaseResponse;
import org.enolj.coffeeordersystem.domain.menu.MenuService;
import org.enolj.coffeeordersystem.domain.menu.dto.MenuResponse;
import org.enolj.coffeeordersystem.domain.menu.dto.PopularMenuResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/menus")
public class MenuController {

    private final MenuService menuService;

    @GetMapping
    public BaseResponse<List<MenuResponse>> getMenus() {
        List<MenuResponse> response = menuService.getMenus();
        return BaseResponse.success("200", "커피 메뉴 목록 조회 성공", response);
    }

    @GetMapping("/popular")
    public BaseResponse<List<PopularMenuResponse>> getPopularMenus() {
        List<PopularMenuResponse> response = menuService.getPopularMenus();
        return BaseResponse.success("200", "최근 7일 인기 메뉴 조회 성공", response);
    }
}
