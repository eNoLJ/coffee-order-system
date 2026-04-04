package org.enolj.coffeeordersystem.domain.menu.service;

import org.enolj.coffeeordersystem.domain.menu.dto.MenuScore;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class MenuRedisServiceTest {

    @Autowired
    private MenuRedisService menuRedisService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @AfterEach
    void tearDown() {
        redisTemplate.getConnectionFactory().getConnection().flushAll();
    }

    @Test
    @DisplayName("최근 7일 인기 메뉴 Top3 조회")
    void getTop3MenusForLast7Days() {
        // given
        LocalDate today = LocalDate.now();

        // menu 1: 5번
        for (int i = 0; i < 5; i++) {
            menuRedisService.increaseScore(1L, today);
        }

        // menu 2: 3번
        for (int i = 0; i < 3; i++) {
            menuRedisService.increaseScore(2L, today);
        }

        // menu 3: 7번
        for (int i = 0; i < 7; i++) {
            menuRedisService.increaseScore(3L, today);
        }

        // when
        List<MenuScore> result = menuRedisService.getTop3MenusForLast7Days();

        // then
        assertThat(result).hasSize(3);

        // 순위 검증
        assertThat(result.get(0).getMenuId()).isEqualTo(3L);
        assertThat(result.get(0).getScore()).isEqualTo(7L);

        assertThat(result.get(1).getMenuId()).isEqualTo(1L);
        assertThat(result.get(1).getScore()).isEqualTo(5L);

        assertThat(result.get(2).getMenuId()).isEqualTo(2L);
        assertThat(result.get(2).getScore()).isEqualTo(3L);
    }
}
