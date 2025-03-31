package com.inventorymanagement.services;

import com.inventorymanagement.exception.InventoryException;

import java.time.LocalDate;

public interface IInventorySheetServices {
    void createInventorySheet(String authHeader, LocalDate startDate, LocalDate endDate) throws InventoryException;
}
