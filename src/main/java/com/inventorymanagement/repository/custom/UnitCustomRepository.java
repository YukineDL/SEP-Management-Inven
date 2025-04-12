package com.inventorymanagement.repository.custom;

import com.inventorymanagement.dto.UnitSearchReqDTO;
import com.inventorymanagement.entity.Unit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UnitCustomRepository {
    Page<Unit> findBySearchReq(UnitSearchReqDTO dto, Pageable pageable);
}
