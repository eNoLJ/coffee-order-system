package org.enolj.coffeeordersystem.domain.menu.service;

import lombok.RequiredArgsConstructor;
import org.enolj.coffeeordersystem.domain.menu.dto.MenuScore;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MenuRedisService {

    private static final String KEY_PREFIX = "popular:menu:";
    private static final String TEMP_KEY_PREFIX = "popular:menu:temp:";

    private final StringRedisTemplate redisTemplate;

    public void increaseScore(Long menuId, LocalDate orderedDate) {
        String key = KEY_PREFIX + orderedDate;

        redisTemplate.opsForZSet().incrementScore(key, String.valueOf(menuId), 1D);
        redisTemplate.expire(key, Duration.ofDays(8));
    }

    public List<MenuScore> getTop3MenusForLast7Days(){
        LocalDate today =  LocalDate.now();

        List<String> keys = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            keys.add(KEY_PREFIX + today.minusDays(i));
        }

        String tempKey = TEMP_KEY_PREFIX + UUID.randomUUID();

        try {
            unionAndStore(keys, tempKey);
            Set<ZSetOperations.TypedTuple<String>> tuples = redisTemplate.opsForZSet().reverseRangeWithScores(tempKey, 0, 2);

            if (tuples == null || tuples.isEmpty()) {
                return Collections.emptyList();
            }

            return tuples.stream()
                    .filter(tuple -> tuple.getValue() != null && tuple.getScore() != null)
                    .map(tuple -> new MenuScore(Long.valueOf(tuple.getValue()), tuple.getScore().longValue()))
                    .collect(Collectors.toList());
        } finally {
            redisTemplate.delete(tempKey);
        }
    }

    private void unionAndStore(List<String> keys, String tempKey) {
        if (keys.isEmpty()) {
            return;
        }

        String firstKey = keys.get(0);
        List<String> otherKeys = keys.subList(1, keys.size());

        if (otherKeys.isEmpty()) {
            redisTemplate.opsForZSet().unionAndStore(firstKey, Collections.emptyList(), tempKey);
        } else {
            redisTemplate.opsForZSet().unionAndStore(firstKey, otherKeys, tempKey);
        }

        redisTemplate.expire(tempKey, Duration.ofSeconds(30));
    }
}
