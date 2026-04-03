package org.enolj.coffeeordersystem.domain.menu.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MenuScore {
    private final Long menuId;
    private final Long score;
}
