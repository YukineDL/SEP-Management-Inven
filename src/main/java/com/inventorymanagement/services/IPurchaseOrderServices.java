package com.inventorymanagement.services;

import com.inventorymanagement.dto.PurchaseDTO;
import com.inventorymanagement.dto.PurchaseOrderCreateDTO;
import com.inventorymanagement.dto.PurchaseOrderReqDTO;
import com.inventorymanagement.dto.response.PurchaseOrderDTO;
import com.inventorymanagement.exception.InventoryException;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

public interface IPurchaseOrderServices {
    void createPurchaseOrder(PurchaseOrderCreateDTO dto) throws InventoryException;
    void approvePurchaseOrder(String authHeader, String purchaseCode, String approveStatus) throws InventoryException;
    void receivePurchaseOrder(String authHeader, String purchaseCode) throws InventoryException;
    PurchaseDTO getPurchaseOrder(String purchaseCode) throws InventoryException;
    Page<PurchaseOrderDTO> findBySearchRequest(PurchaseOrderReqDTO reqDTO);
    @Transactional
    void updatePurchaseOrder(PurchaseOrderCreateDTO createDTO, String purchaseOrderCode) throws InventoryException;
}
