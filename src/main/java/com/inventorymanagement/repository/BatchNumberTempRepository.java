package com.inventorymanagement.repository;

import com.inventorymanagement.entity.BatchNumberTemp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BatchNumberTempRepository extends JpaRepository<BatchNumberTemp, Integer> {
    List<BatchNumberTemp> findByInventoryReceiptCode(String code);
    void deleteByInventoryReceiptCode(String code);
}
