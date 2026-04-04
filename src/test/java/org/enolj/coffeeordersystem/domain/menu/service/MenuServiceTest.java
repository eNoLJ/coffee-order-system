package org.enolj.coffeeordersystem.domain.menu.service;

import org.enolj.coffeeordersystem.domain.menu.MenuService;
import org.enolj.coffeeordersystem.domain.menu.dto.PopularMenuResponse;
import org.enolj.coffeeordersystem.domain.menu.entity.Menu;
import org.enolj.coffeeordersystem.domain.menu.entity.MenuStatus;
import org.enolj.coffeeordersystem.domain.menu.repository.MenuRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class MenuServiceTest {

    @Autowired
    private MenuService menuService;

    @Autowired
    private MenuRedisService menuRedisService;

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Test
    @DisplayName("인기 메뉴 조회 - Redis + DB 통합 테스트")
    void getPopularMenus() {
        // given
        LocalDate today = LocalDate.now();

        Menu menu1 = createMenu("아메리카노", 3000L);
        Menu menu2 = createMenu("카페라떼", 4000L);
        Menu menu3 = createMenu("콜드브루", 4500L);

        // Redis 점수 세팅
        for (int i = 0; i < 10; i++) menuRedisService.increaseScore(menu1.getId(), today);
        for (int i = 0; i < 5; i++) menuRedisService.increaseScore(menu2.getId(), today);
        for (int i = 0; i < 7; i++) menuRedisService.increaseScore(menu3.getId(), today);

        // when
        List<PopularMenuResponse> result = menuService.getPopularMenus();

        // then
        assertThat(result).hasSize(3);

        // 1등
        assertThat(result.get(0).getMenuId()).isEqualTo(menu1.getId());
        assertThat(result.get(0).getOrderCount()).isEqualTo(10L);

        // 2등
        assertThat(result.get(1).getMenuId()).isEqualTo(menu3.getId());
        assertThat(result.get(1).getOrderCount()).isEqualTo(7L);

        // 3등
        assertThat(result.get(2).getMenuId()).isEqualTo(menu2.getId());
        assertThat(result.get(2).getOrderCount()).isEqualTo(5L);
    }

    private Menu createMenu(String name, Long price) {
        Menu menu = new Menu();

        ReflectionTestUtils.setField(menu, "name", name);
        ReflectionTestUtils.setField(menu, "price", price);
        ReflectionTestUtils.setField(menu, "status", MenuStatus.ON_SALE);

        return menuRepository.save(menu);
    }
}
