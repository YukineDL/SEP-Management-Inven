package com.inventorymanagement.services;

import com.inventorymanagement.dto.InventoryDTO;
import com.inventorymanagement.exception.InventoryException;

public interface IInventoryServices {
    void createInventory(InventoryDTO inventoryDTO, String authHeader) throws InventoryException;
}
