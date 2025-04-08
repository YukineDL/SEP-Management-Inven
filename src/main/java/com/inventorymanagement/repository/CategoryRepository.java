package com.inventorymanagement.repository;

import com.inventorymanagement.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
    boolean existsByCode(String code);
    Optional<Category> findByCode(String code);
    boolean existsByCodeAndCodeNotLike(String code, String oldCode);
}
