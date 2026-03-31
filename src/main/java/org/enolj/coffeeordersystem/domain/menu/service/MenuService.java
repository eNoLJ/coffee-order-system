package org.enolj.coffeeordersystem.domain.menu;

import lombok.RequiredArgsConstructor;
import org.enolj.coffeeordersystem.domain.menu.dto.MenuResponse;
import org.enolj.coffeeordersystem.domain.menu.entity.MenuStatus;
import org.enolj.coffeeordersystem.domain.menu.repository.MenuRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MenuService {

    private final MenuRepository menuRepository;

    @Cacheable(value = "menuListCache", key = "'allMenus'")
    public List<MenuResponse> getMenus() {
        return menuRepository.findAllByStatusOrderByIdAsc(MenuStatus.ON_SALE)
                .stream()
                .map(MenuResponse::from)
                .toList();
    }
}
