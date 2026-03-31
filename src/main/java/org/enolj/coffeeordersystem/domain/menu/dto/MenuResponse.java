package org.enolj.coffeeordersystem.domain.menu.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.enolj.coffeeordersystem.domain.menu.entity.Menu;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class MenuResponse {

    private Long menuId;
    private String name;
    private Long price;

    public static MenuResponse from(Menu menu) {
        return new MenuResponse(
                menu.getId(),
                menu.getName(),
                menu.getPrice()
        );
    }
}
