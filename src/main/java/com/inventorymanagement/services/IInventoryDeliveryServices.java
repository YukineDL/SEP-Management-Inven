package com.inventorymanagement.services;


import com.inventorymanagement.dto.InventoryDeliveryCreateDTO;
import com.inventorymanagement.dto.InventoryDeliveryDTO;
import com.inventorymanagement.dto.InventoryDeliverySearchReqDTO;
import com.inventorymanagement.exception.InventoryException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IInventoryDeliveryServices {
    void createInventoryDeliveryByOrderCode(String authHeader, String orderCode, InventoryDeliveryCreateDTO dto) throws InventoryException;
    void approveInventoryDelivery(String authHeader, String inventoryDeliveryCode) throws InventoryException;
    void rejectInventoryDelivery(String authHeader, String inventoryDeliveryCode) throws InventoryException;
    Page<InventoryDeliveryDTO> findBySearchRequest(InventoryDeliverySearchReqDTO reqDTO, Pageable pageable) throws InventoryException;
    InventoryDeliveryDTO findByCode(String inventoryDeliveryCode) throws InventoryException;
    void createInventoryDeliveryReturn(String authHeader, String returnCode) throws InventoryException;
}
