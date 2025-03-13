package com.inventorymanagement.repository;

import com.inventorymanagement.entity.ProcessCheck;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProcessCheckRepository extends JpaRepository<ProcessCheck, Integer> {
    Optional<ProcessCheck> findByCheckSync(String key);
}
