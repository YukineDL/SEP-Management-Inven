package com.inventorymanagement.repository.custom;

import com.inventorymanagement.dto.ReturnFormSearchReq;
import com.inventorymanagement.entity.ReturnForm;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReturnFormCustomRepository {
    Page<ReturnForm> findBySearchReq(ReturnFormSearchReq req, Pageable pageable);
}
