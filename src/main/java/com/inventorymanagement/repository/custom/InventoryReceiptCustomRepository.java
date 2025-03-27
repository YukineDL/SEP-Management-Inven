package com.inventorymanagement.repository.custom;

import com.inventorymanagement.dto.InventoryReceiptSearchReq;
import com.inventorymanagement.entity.InventoryReceipt;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface InventoryReceiptCustomRepository {
    Page<InventoryReceipt> findBySearchRequest(InventoryReceiptSearchReq req, Pageable pageable);
}
