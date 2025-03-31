package com.inventorymanagement.repository;

import com.inventorymanagement.entity.ProductSheet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductSheetRepository extends JpaRepository<ProductSheet, Integer> {
}
