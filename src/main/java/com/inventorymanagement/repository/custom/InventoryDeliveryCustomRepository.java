package com.inventorymanagement.repository.custom;

import com.inventorymanagement.dto.InventoryDeliveryDTO;
import com.inventorymanagement.dto.InventoryDeliverySearchReqDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface InventoryDeliveryCustomRepository {
    Page<InventoryDeliveryDTO> findBySearchRequest(Pageable pageable, InventoryDeliverySearchReqDTO reqDTO);
}
