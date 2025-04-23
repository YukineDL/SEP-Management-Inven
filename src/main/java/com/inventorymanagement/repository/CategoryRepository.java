package com.inventorymanagement.repository;

import com.inventorymanagement.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
    boolean existsByCode(String code);
    Optional<Category> findByCode(String code);
    boolean existsByCodeAndCodeNotLike(String code, String oldCode);
    Page<Category> findByIsDeletedOrIsDeletedIsNull(boolean isDeleted, Pageable pageable);
}
