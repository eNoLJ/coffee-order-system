package org.enolj.coffeeordersystem.domain.menu.repository;

import org.enolj.coffeeordersystem.domain.menu.entity.Menu;
import org.enolj.coffeeordersystem.domain.menu.entity.MenuStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MenuRepository extends JpaRepository<Menu, String> {

    List<Menu> findAllByStatusOrderByIdAsc(MenuStatus status);
}
