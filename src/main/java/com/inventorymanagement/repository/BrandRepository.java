package com.inventorymanagement.repository;

import com.inventorymanagement.entity.Brand;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BrandRepository extends JpaRepository<Brand, Integer> {
    boolean existsByCode(String code);
    Optional<Brand> findByCode(String code);
}
