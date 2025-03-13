package com.inventorymanagement.repository.custom;

import com.inventorymanagement.dto.PurchaseOrderReqDTO;
import com.inventorymanagement.entity.PurchaseOrder;
import org.springframework.data.domain.Page;


public interface PurchaseOrderCustomRepository {
    Page<PurchaseOrder> findBySearchRequest(PurchaseOrderReqDTO searchReq);
}
