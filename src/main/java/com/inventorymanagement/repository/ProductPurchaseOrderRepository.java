package com.inventorymanagement.repository;

import com.inventorymanagement.entity.ProductPurchaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProductPurchaseOrderRepository extends JpaRepository<ProductPurchaseOrder, Integer> {
    void deleteByPurchaseOrderCode(String purchaseOrderCode);
}
