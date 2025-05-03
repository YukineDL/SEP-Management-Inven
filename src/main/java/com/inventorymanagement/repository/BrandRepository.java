package com.inventorymanagement.repository;

import com.inventorymanagement.entity.Brand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BrandRepository extends JpaRepository<Brand, Integer> {
    boolean existsByCodeAndIsDeleted(String code, Boolean isDelete);
    Optional<Brand> findByCodeAndIsDeleted(String code, Boolean isDelete);
    boolean existsByNameAndCodeNotLike(String brandName, String code);
    Page<Brand> findByIsDeletedOrIsDeletedIsNull(Boolean isDeleted, Pageable pageable);
}
