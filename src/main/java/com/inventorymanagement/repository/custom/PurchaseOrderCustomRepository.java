package com.inventorymanagement.repository.custom;

import com.inventorymanagement.dto.PurchaseOrderReqDTO;
import com.inventorymanagement.dto.response.PurchaseOrderDTO;
import org.springframework.data.domain.Page;


public interface PurchaseOrderCustomRepository {
    Page<PurchaseOrderDTO> findBySearchRequest(PurchaseOrderReqDTO searchReq);
}
