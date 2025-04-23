package com.inventorymanagement.services;

import com.inventorymanagement.dto.InventorySheetDTO;
import com.inventorymanagement.dto.InventorySheetSearchDTO;
import com.inventorymanagement.dto.ProductSheetDTO;
import com.inventorymanagement.dto.ProductSheetSearchReqDTO;
import com.inventorymanagement.entity.InventorySheet;
import com.inventorymanagement.exception.InventoryException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface IInventorySheetServices {
    String createInventorySheet(String authHeader, LocalDate startDate, LocalDate endDate) throws InventoryException;
    InventorySheetDTO getDetailInventorySheetBySearchRequest(Pageable pageable, ProductSheetSearchReqDTO dto) throws InventoryException;
    Page<InventorySheet> findBySearchRequest(Pageable pageable, InventorySheetSearchDTO dto) ;
    void reviewInventorySheet(String authHeader, String code) throws InventoryException;
    byte[] exportExcel(ProductSheetSearchReqDTO dto) throws InventoryException;
}
