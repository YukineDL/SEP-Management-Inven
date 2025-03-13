package com.inventorymanagement.repository.custom;

import com.inventorymanagement.dto.ProductPurchaseDTO;

import java.util.List;

public interface ProductPurchaseCustomRepository {
    List<ProductPurchaseDTO> findByPurchaseOrderCode(String purchaseOrderCode);
}
