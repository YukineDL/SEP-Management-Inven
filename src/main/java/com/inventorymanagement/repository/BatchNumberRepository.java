package com.inventorymanagement.repository;

import com.inventorymanagement.entity.BatchNumber;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface BatchNumberRepository extends JpaRepository<BatchNumber, Integer> {
    List<BatchNumber> findByInventoryReceiptCode(String code);
    List<BatchNumber> findByProductCodeInAndStatusOrderByCreateAtAsc(List<String> productCode, String status);
}