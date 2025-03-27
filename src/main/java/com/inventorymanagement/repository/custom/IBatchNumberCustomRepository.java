package com.inventorymanagement.repository.custom;

import com.inventorymanagement.dto.BatchNumberDTO;

import java.util.List;

public interface IBatchNumberCustomRepository {
    List<BatchNumberDTO> findAllByInventoryReceiptCode(String inventoryReceiptCode);
}
