package com.inventorymanagement.services;

import com.inventorymanagement.dto.InventoryReceiptDTO;
import com.inventorymanagement.dto.InventoryReceiptReqDTO;
import com.inventorymanagement.dto.InventoryReceiptResDTO;
import com.inventorymanagement.dto.InventoryReceiptSearchReq;
import com.inventorymanagement.exception.InventoryException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

public interface IInventoryReceiptServices {
    String createReceipt(InventoryReceiptReqDTO receiptReqDTO) throws InventoryException;
    void approveInventoryReceipt(String authHeader, String inventoryReceiptCode) throws InventoryException;
    @Transactional
    void rejectInventoryReceipt(String authHeader, String inventoryReceiptCode) throws InventoryException;
    Page<InventoryReceiptResDTO> findBySearchRequest(InventoryReceiptSearchReq req, Pageable pageable) throws InventoryException;
    InventoryReceiptDTO findByCode(String inventoryCode) throws InventoryException;
    @Transactional
    void updateInventoryReceipt(String inventoryReceiptCode, InventoryReceiptReqDTO dto) throws InventoryException;
}
