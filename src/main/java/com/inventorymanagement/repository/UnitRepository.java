package com.inventorymanagement.repository;

import com.inventorymanagement.entity.Unit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UnitRepository extends JpaRepository<Unit, Integer> {
    boolean existsByCode(String code);
    boolean existsByCodeAndCodeNotLike(String code, String codeNotLike);
    Optional<Unit> findByCode(String code);
}
