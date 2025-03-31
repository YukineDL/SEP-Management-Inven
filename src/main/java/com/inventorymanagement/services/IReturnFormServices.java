package com.inventorymanagement.services;

import com.inventorymanagement.dto.ReturnFormCreateDTO;
import com.inventorymanagement.dto.ReturnFormDTO;
import com.inventorymanagement.dto.ReturnFormSearchReq;
import com.inventorymanagement.entity.ReturnForm;
import com.inventorymanagement.exception.InventoryException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IReturnFormServices {
    void createReturnForm(ReturnFormCreateDTO dto) throws InventoryException;
    void approveReturnForm(String returnCode) throws InventoryException;
    void rejectReturnForm(String returnCode) throws InventoryException;
    Page<ReturnForm> findBySearchRequest(ReturnFormSearchReq dto, Pageable pageable);
    ReturnFormDTO findReturnForm(String returnCode) throws InventoryException;
}
