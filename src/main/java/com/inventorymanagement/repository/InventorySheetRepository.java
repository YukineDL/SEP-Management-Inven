package com.inventorymanagement.repository;

import com.inventorymanagement.entity.InventorySheet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface InventorySheetRepository extends JpaRepository<InventorySheet, Integer> {
    List<InventorySheet> findByStartDateGreaterThanEqualAndEndDateLessThanEqual(LocalDate startDate, LocalDate endDate);
}
