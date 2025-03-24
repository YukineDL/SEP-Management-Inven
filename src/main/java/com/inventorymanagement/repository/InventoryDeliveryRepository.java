package com.inventorymanagement.repository;

import com.inventorymanagement.entity.InventoryDelivery;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InventoryDeliveryRepository extends JpaRepository<InventoryDelivery, Integer> {
    Optional<InventoryDelivery> findByCode(String code);
}
