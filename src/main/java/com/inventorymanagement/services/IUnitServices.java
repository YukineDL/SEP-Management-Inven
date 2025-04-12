package com.inventorymanagement.services;

import com.inventorymanagement.dto.UnitCreateReqDTO;
import com.inventorymanagement.dto.UnitSearchReqDTO;
import com.inventorymanagement.entity.Unit;
import com.inventorymanagement.exception.InventoryException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IUnitServices {
    void createUnit(String authHeader, UnitCreateReqDTO dto) throws InventoryException;
    void updateUnit(String authHeader, UnitCreateReqDTO dto, String code) throws InventoryException;
    void deleteUnit(String authHeader, String code) throws InventoryException;
    Unit getUnit(String code) throws InventoryException;
    Page<Unit> getAllUnits(UnitSearchReqDTO dto, Pageable pageable)  ;
}
