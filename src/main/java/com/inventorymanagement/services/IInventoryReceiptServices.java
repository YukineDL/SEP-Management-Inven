package com.inventorymanagement.services;

import com.inventorymanagement.dto.InventoryReceiptReqDTO;
import com.inventorymanagement.exception.InventoryException;

public interface IInventoryReceiptServices {
    String createReceipt(InventoryReceiptReqDTO receiptReqDTO) throws InventoryException;
}
