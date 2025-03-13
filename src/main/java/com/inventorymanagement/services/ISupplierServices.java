package com.inventorymanagement.services;

import com.inventorymanagement.dto.SupplierDTO;
import com.inventorymanagement.entity.Supplier;
import com.inventorymanagement.exception.InventoryException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ISupplierServices {
    void create(SupplierDTO supplierDTO) throws InventoryException;
    void update(SupplierDTO dto, Integer id) throws InventoryException;
    Page<Supplier> findAll(Pageable pageable);
    void deleteById(Integer id, Boolean isDeleted) throws InventoryException;
    Supplier findById(Integer id) throws InventoryException;
}
