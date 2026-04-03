package org.enolj.coffeeordersystem.domain.menu;

import lombok.RequiredArgsConstructor;
import org.enolj.coffeeordersystem.domain.menu.dto.MenuResponse;
import org.enolj.coffeeordersystem.domain.menu.dto.MenuScore;
import org.enolj.coffeeordersystem.domain.menu.dto.PopularMenuResponse;
import org.enolj.coffeeordersystem.domain.menu.entity.Menu;
import org.enolj.coffeeordersystem.domain.menu.entity.MenuStatus;
import org.enolj.coffeeordersystem.domain.menu.repository.MenuRepository;
import org.enolj.coffeeordersystem.domain.menu.service.MenuRedisService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MenuService {

    private final MenuRepository menuRepository;
    private final MenuRedisService menuRedisService;

    @Cacheable(value = "menuListCache", key = "'allMenus'")
    public List<MenuResponse> getMenus() {
        return menuRepository.findAllByStatusOrderByIdAsc(MenuStatus.ON_SALE)
                .stream()
                .map(MenuResponse::from)
                .toList();
    }

    public List<PopularMenuResponse> getPopularMenus() {
        List<MenuScore> topMenus = menuRedisService.getTop3MenusForLast7Days();

        if (topMenus.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> menuIds = topMenus.stream()
                .map(MenuScore::getMenuId)
                .toList();

        Map<Long, Menu> menuMap = menuRepository.findAllByIdIn(menuIds).stream()
                .collect(Collectors.toMap(Menu::getId, menu -> menu));

        List<PopularMenuResponse> responses = new ArrayList<>();

        long rank = 1;
        for (MenuScore menuScore : topMenus) {
            Menu menu = menuMap.get(menuScore.getMenuId());
            if (menu == null) {
                continue;
            }

            responses.add(PopularMenuResponse.from(
                    rank++,
                    menu.getId(),
                    menu.getName(),
                    menu.getPrice(),
                    menuScore.getScore()
                )
            );
        }

        return responses;
    }
}
