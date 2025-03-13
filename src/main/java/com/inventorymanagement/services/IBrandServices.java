package com.inventorymanagement.services;

import com.inventorymanagement.dto.BrandDTO;
import com.inventorymanagement.entity.Brand;
import com.inventorymanagement.exception.InventoryException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IBrandServices {
    void create(String authHeader, BrandDTO brandDTO) throws InventoryException;
    void update(String authHeader, BrandDTO brandDTO, String brandCode) throws InventoryException;
    Page<Brand> findAll( Pageable pageable) ;
}
