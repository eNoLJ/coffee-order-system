package org.enolj.coffeeordersystem.domain.menu.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class PopularMenuResponse {

    private Long rank;
    private Long menuId;
    private String menuName;
    private Long price;
    private Long orderCount;

    public static PopularMenuResponse from(Long rank, Long menuId, String menuName, Long price, Long orderCount) {
        return PopularMenuResponse.builder()
                .rank(rank)
                .menuId(menuId)
                .menuName(menuName)
                .price(price)
                .orderCount(orderCount)
                .build();
    }
}
