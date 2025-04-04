package com.inventorymanagement.repository;

import com.inventorymanagement.entity.InventoryReceipt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InventoryReceiptRepository extends JpaRepository<InventoryReceipt, Integer> {
    boolean existsByPurchaseOrderCodeAndApproveIn(String purchaseOrderCode, List<String> approveIn);
    Optional<InventoryReceipt> findByCode(String code);
}
