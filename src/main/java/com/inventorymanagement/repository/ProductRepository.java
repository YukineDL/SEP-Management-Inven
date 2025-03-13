package com.inventorymanagement.repository;

import com.inventorymanagement.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Integer> {
    Boolean existsByName(String name);
    Optional<Product> findByCode(String code);
}
