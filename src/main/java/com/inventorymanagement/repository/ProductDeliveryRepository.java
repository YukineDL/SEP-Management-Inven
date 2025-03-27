package com.inventorymanagement.repository;

import com.inventorymanagement.entity.ProductDelivery;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductDeliveryRepository extends JpaRepository<ProductDelivery, Integer> {
    List<ProductDelivery> findByInventoryDeliveryCode(String inventoryDeliveryCode);
}
