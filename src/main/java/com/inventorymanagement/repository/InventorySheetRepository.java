package com.inventorymanagement.repository;

import com.inventorymanagement.entity.InventorySheet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface InventorySheetRepository extends JpaRepository<InventorySheet, Integer> {
    List<InventorySheet> findByStartDateAndEndDate(LocalDate startDate, LocalDate endDate);
    Optional<InventorySheet> findByCode(String code);
}
