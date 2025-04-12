package com.inventorymanagement.repository;

import com.inventorymanagement.entity.ReturnProduct;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReturnProductRepository extends JpaRepository<ReturnProduct, Integer> {
    List<ReturnProduct> findByReturnFormCode(String returnFormCode);
    List<ReturnProduct> findByReturnFormCodeIn(List<String> codes);
}
