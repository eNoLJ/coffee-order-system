package org.enolj.coffeeordersystem.domain.menu.repository;

import org.enolj.coffeeordersystem.domain.menu.entity.Menu;
import org.enolj.coffeeordersystem.domain.menu.entity.MenuStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MenuRepository extends JpaRepository<Menu, String> {

    Optional<Menu> findById(Long id);

    List<Menu> findAllByStatusOrderByIdAsc(MenuStatus status);

    List<Menu> findAllByIdIn(List<Long> ids);
}
