package com.inventorymanagement.repository.custom;


import com.inventorymanagement.dto.BatchNumberDTO;

import java.util.List;

public interface BatchNumberTempCustomRepository {
    List<BatchNumberDTO> findBatchNumberTempByCode(String inventoryCode);
}
