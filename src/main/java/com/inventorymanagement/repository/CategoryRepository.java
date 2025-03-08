package com.inventorymanagement.repository;

import com.inventorymanagement.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
    boolean existsByCode(String code);
    Category findByCode(String code);
}
