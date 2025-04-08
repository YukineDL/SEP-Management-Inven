package com.inventorymanagement.repository.custom;

import com.inventorymanagement.dto.InventorySheetSearchDTO;
import com.inventorymanagement.entity.InventorySheet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface InventorySheetCustomRepository {
    Page<InventorySheet> findBySearchReq(InventorySheetSearchDTO req, Pageable pageable);
}
