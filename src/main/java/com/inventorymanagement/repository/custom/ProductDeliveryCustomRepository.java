package com.inventorymanagement.repository.custom;

import com.inventorymanagement.dto.InventoryDeliveryDTO;
import com.inventorymanagement.dto.ProductDeliveryDTO;

import java.util.List;

public interface ProductDeliveryCustomRepository {
    List<ProductDeliveryDTO> findByInventoryDeliveryCode(String inventoryCode);
}
