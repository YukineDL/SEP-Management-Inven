package com.inventorymanagement.services;

import com.inventorymanagement.dto.ReturnFormCreateDTO;
import com.inventorymanagement.dto.ReturnFormDTO;
import com.inventorymanagement.dto.ReturnFormSearchReq;
import com.inventorymanagement.entity.ReturnForm;
import com.inventorymanagement.exception.InventoryException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IReturnFormServices {
    void createReturnForm(ReturnFormCreateDTO dto, String authHeader) throws InventoryException;
    void approveReturnForm(String authHeader, String returnCode) throws InventoryException;
    void rejectReturnForm(String authHeader,String returnCode) throws InventoryException;
    Page<ReturnFormDTO> findBySearchRequest(ReturnFormSearchReq dto, Pageable pageable, String authHeader) throws InventoryException;
    ReturnFormDTO findReturnForm(String returnCode) throws InventoryException;
}
