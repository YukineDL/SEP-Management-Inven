package com.inventorymanagement.repository;

import com.inventorymanagement.entity.BatchNumber;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BatchNumberRepository extends JpaRepository<BatchNumber, Integer> {
}
